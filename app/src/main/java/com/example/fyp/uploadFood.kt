package com.example.fyp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class uploadFood : AppCompatActivity() {

    private lateinit var calEditText: TextView
    private lateinit var nameEditText: EditText
    private lateinit var carEditText: EditText
    private lateinit var proEditText: EditText
    private lateinit var fatEditText: EditText
    private lateinit var weightEditText: TextView
    private lateinit var qtyEditText: TextView
    private lateinit var image: ImageView
    private lateinit var uploadButton: Button
    var imageURL: String? = null
    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_food)

        // Initialize EditTexts
        nameEditText = findViewById(R.id.name_input)
        calEditText = findViewById(R.id.calories_input)
        carEditText = findViewById(R.id.carbohydrate_input)
        proEditText = findViewById(R.id.protein_input)
        fatEditText = findViewById(R.id.fat_input)
        weightEditText = findViewById(R.id.weight_input)
        qtyEditText = findViewById(R.id.quantity_input)
        uploadButton = findViewById(R.id.submit_button)
        calEditText.text = "0"
        weightEditText.text = "0"
        qtyEditText.text = "1"
        weightEditText.text = "1"

        // Initialize ImageView
        image = findViewById(R.id.uploadImage)

        // Register Activity Result Launcher
        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                uri = data?.data ?: return@registerForActivityResult
                image.setImageURI(uri)
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Set OnClickListener for ImageView
        image.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            activityResultLauncher.launch(photoPicker)
        }

        // Set OnClickListener for Upload Button
        uploadButton.setOnClickListener {
            saveData()
        }

        // Add TextWatchers to calculate calories
        carEditText.addTextChangedListener(textWatcher)
        proEditText.addTextChangedListener(textWatcher)
        fatEditText.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            calculateCalories()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun calculateCalories() {
        val carbs = carEditText.text.toString().toIntOrNull() ?: 0
        val protein = proEditText.text.toString().toIntOrNull() ?: 0
        val fat = fatEditText.text.toString().toIntOrNull() ?: 0

        val calories = (carbs * 4) + (protein * 4) + (fat * 9)
        calEditText.text = calories.toString()
    }

    private fun saveData() {
        // Check if uri is null
        if (uri == null) {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference
            .child("Android Images/${uri!!.lastPathSegment}")

        // Upload image
        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val urlImage = task.result
                    imageURL = urlImage.toString()
                    uploadData() // Call to upload food data
                }
            }
        }.addOnFailureListener { e ->
            Log.e("UploadError", "Error uploading image: ${e.message}")
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadData() {
        val name = nameEditText.text.toString()
        val protein = proEditText.text.toString()
        val carbohydrates = carEditText.text.toString()
        val fat = fatEditText.text.toString()
        val weight = weightEditText.text.toString()
        val qty = qtyEditText.text.toString()
        val favour = "false"
        val cal = calEditText.text.toString()

        // Create a Food object
        val dataClass = Food(name, protein, carbohydrates, fat, weight, qty, favour, imageURL, cal, "")

        Log.d("UploadData", "Data Class: $dataClass")

        // Use a unique key for each entry
        val key = FirebaseDatabase.getInstance().reference.child("Demo Food").push().key
        if (key != null) {
            dataClass.key = key
            FirebaseDatabase.getInstance().reference
                .child("Demo Food")
                .child(name)
                .setValue(dataClass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, homeActivity::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener { e ->
                    Log.e("UploadError", "Error uploading data: ${e.message}")
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Failed to generate unique key", Toast.LENGTH_SHORT).show()
        }
    }
}