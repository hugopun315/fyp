package com.example.fyp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class foodDetails : AppCompatActivity() {
    private var key: String = ""
    private var imageUrl: String = ""
    private var fav : String = ""
    private var qty : String = ""
    private var foodTitle : String = ""
    private var foodWeight  : String = ""
    private var foodCar : String = ""
    private var foodPro : String = ""
    private var foodFat : String = ""
    private var foodCal : String = ""
    private var time : String = ""
    private var date : String = ""
    private var value : String = ""
    private var auth= FirebaseAuth.getInstance()
    private lateinit var decreaseQty: Button
    private lateinit var increaseQty: Button
    private lateinit var quantityEditText: EditText
    private lateinit var addButton: Button
    val currentUser = auth.currentUser
    val userID = currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)
        val weight: TextView = findViewById(R.id.weight)
        val title: TextView = findViewById(R.id.titleTextView)
        val image: ImageView = findViewById(R.id.foodPic)
        val cal: TextView = findViewById(R.id.Calories)
        val car: TextView = findViewById(R.id.carbohydrate)
        val pro: TextView = findViewById(R.id.protein)
        val fat: TextView = findViewById(R.id.fat)
        decreaseQty = findViewById(R.id.decreaseQty)
        increaseQty = findViewById(R.id.increaseQty)
        quantityEditText = findViewById(R.id.quantity)



        addButton = findViewById(R.id.addRecord)
        decreaseQty.setOnClickListener {
            val currentQty = quantityEditText.text.toString().toInt()
            if (currentQty > 1) {
                quantityEditText.setText((currentQty - 1).toString())
            }
        }
        increaseQty.setOnClickListener {
            val currentQty = quantityEditText.text.toString().toInt()
            quantityEditText.setText((currentQty + 1).toString())
        }
        val bundle = intent.extras
        if (bundle != null) {
            title.text =  bundle.getString("title") ?: "No Title"
            cal.text = "calories: " + bundle.getString("cal") + " kcal" ?: "0"
            car.text = "carbohydrates: " + bundle.getString("car") + " g" ?: "0"
            pro.text = "protein: " + bundle.getString("pro") + " g" ?: "0"
            fat.text = "fat: " + bundle.getString("fat") + " g" ?: "0"
            weight.text = "weight: " + bundle.getString("weight") + " g" ?: "0"
            key = bundle.getString("key") ?: ""
            imageUrl = bundle.getString("image") ?: ""
            fav = bundle.getString("fav") ?: ""
            qty = bundle.getString("qty") ?: ""
            foodTitle = bundle.getString("title") ?: ""
            foodWeight = bundle.getString("weight") ?: ""
            foodCar = bundle.getString("car") ?: ""
            foodPro = bundle.getString("pro") ?: ""
            foodFat= bundle.getString("fat") ?: ""
            foodCal = bundle.getString("cal") ?: ""
            time = bundle.getString("time") ?: ""
            date= bundle.getString("date") ?: ""
            value = bundle.getString("value") ?: ""

            if (value == "R"){
                addButton.text = "remove"
            }

            // Load the image only if imageUrl is not empty
            if (imageUrl.isNotEmpty()) {
                Glide.with(this).load(imageUrl).into(image)
            } else {
                // Handle the case when there is no image URL
                image.setImageResource(R.drawable.apple_158989157) // Set a placeholder image
            }
        } else {
            // Handle the case when bundle is null
            Log.e("foodDetails", "Bundle is null")
        }


        addButton.setOnClickListener {
            if(addButton.text == "remove"){
                removeFood()

            }
            else{
                uploadFood()
            }

        }


    }


    private fun uploadFood() {
        val name = foodTitle
        val protein = foodPro
        val carbohydrates = foodCar
        val fat = foodFat
        val weight = foodWeight
        val qty = qty
        val favour = "false"
        val cal = foodCal.toIntOrNull() ?: 0
        val imageURL = imageUrl
        val foodKey = key
        val date = date
        val time = time
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        // Create a Food object
        val dataClass = Food(name, protein, carbohydrates, fat, weight, qty, favour, imageURL, cal.toString(), foodKey)

        Log.d("UploadData", "Data Class: $dataClass")

        // Use a unique key for each entry
        val key = FirebaseDatabase.getInstance().reference.child("Demo Food").push().key
        if (key != null) {
            if (userID != null) {
                val userMealsRef = FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(userID)
                    .child("meals")
                    .child(date)
                    .child(time)
                    .child("Food")
                    .child(name)

                userMealsRef.setValue(dataClass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                            // Update total calories
                            updateTotalCalories(userID, date)
                        }
                    }.addOnFailureListener { e ->
                        Log.e("UploadError", "Error uploading data: ${e.message}")
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Failed to generate unique key", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFood() {
        val name = foodTitle
        val date = date
        val time = time
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        if (userID != null) {
            val userMealsRef = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(userID)
                .child("meals")
                .child(date)
                .child(time)
                .child("Food")
                .child(name)



            userMealsRef.removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
                        // Update total calories
                        updateTotalCalories(userID, date)
                    }
                }.addOnFailureListener { e ->
                    Log.e("RemoveError", "Error removing data: ${e.message}")
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }


        }
    }

    private fun updateTotalCalories(userId: String, date: String) {
        val mealsRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)
            .child("meals")
            .child(date)

        mealsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalCalories = 0

                for (mealSnapshot in dataSnapshot.children) {
                    if (mealSnapshot.key != "record") {
                        for (foodSnapshot in mealSnapshot.child("Food").children) {
                            val calories = foodSnapshot.child("calories").getValue(String::class.java)?.toIntOrNull() ?: 0
                            totalCalories += calories
                        }
                    }
                }

                mealsRef.child("record").setValue(totalCalories.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data: ${databaseError.message}")
            }
        })

        val context: Context = this
        val intent = Intent(this, FindFoodView::class.java).apply {
            putExtra("time", time)
        }
        context.startActivity(intent)
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}