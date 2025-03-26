package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class changeBodyData : AppCompatActivity() {
    private lateinit var hightlEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var changeButton: Button
    private lateinit var ageEditText: EditText
    private lateinit var habitRadio: RadioGroup
    private lateinit var targetRadio: RadioGroup
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var sex: String
    private lateinit var key: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_body_data)
        habitRadio = findViewById(R.id.exercise_list)
        targetRadio = findViewById(R.id.targetList)
        hightlEditText = findViewById(R.id.inputHight)
        weightEditText = findViewById(R.id.inputWeight)
        ageEditText = findViewById(R.id.inputAge)
        changeButton = findViewById(R.id.btnCreate)
        auth = FirebaseAuth.getInstance()
        var selectedTarget: String? = null
        var selectedHabit: String? = null
        habitRadio.setOnCheckedChangeListener { group, checkedId ->
            selectedHabit = when (checkedId) {
                R.id.noExercise -> "No Exercise"
                R.id.lightExercise -> "Light Exercise"
                R.id.middleExercise -> "Middle Exercise"
                R.id.heavyExercise -> "Heavy Exercise"
                R.id.veryheavyExercise -> "Very Heavy Exercise"
                else -> null
            }

            Toast.makeText(
                baseContext,
                selectedHabit,
                Toast.LENGTH_SHORT,
            ).show()
        }

        targetRadio.setOnCheckedChangeListener { group, checkedId ->
            selectedTarget = when (checkedId) {
                R.id.LoseWeight -> "Lose Weight"
                R.id.GainWeight -> "Gain Weight"
                R.id.MaintainWeight -> "Maintain Weight"
                else -> null
            }

            Toast.makeText(
                baseContext,
                selectedTarget,
                Toast.LENGTH_SHORT,
            ).show()
        }

        val currentUser = auth.currentUser

        val userID = currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")
        fetchUserProfile(databaseReference, hightlEditText , weightEditText , ageEditText , habitRadio ,targetRadio)



        changeButton.setOnClickListener {
            val loadingProgressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
            loadingProgressBar.visibility = View.VISIBLE // Show loading indicator

            // Get the input values
            val height = hightlEditText.text.toString()
            val weight = weightEditText.text.toString()
            val age = ageEditText.text.toString()
            val target = selectedTarget
            val habit = selectedHabit

            // Get the currently logged-in user
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(baseContext, "No user logged in.", Toast.LENGTH_SHORT).show()
                loadingProgressBar.visibility = View.GONE
                return@setOnClickListener
            }

            val userId = currentUser.uid

            // Calculate BMR, TDEE, and target calories
            var BMR = 0.0
            if (sex == "Male") {
                BMR = (weight.toDoubleOrNull() ?: 0.0) * 10 + 6.25 * (height.toDoubleOrNull() ?: 0.0) - 5 * (age.toDoubleOrNull() ?: 0.0) + 5
            } else {
                BMR = (weight.toDoubleOrNull() ?: 0.0) * 10 + 6.25 * (height.toDoubleOrNull() ?: 0.0) - 5 * (age.toDoubleOrNull() ?: 0.0) - 161
            }

            var TDEE = 0.0
            when (selectedHabit) {
                "No Exercise" -> TDEE = (BMR * 1.2).roundToTwoDecimalPlaces()
                "Light Exercise" -> TDEE = (BMR * 1.4).roundToTwoDecimalPlaces()
                "Middle Exercise" -> TDEE = (BMR * 1.6).roundToTwoDecimalPlaces()
                "Heavy Exercise" -> TDEE = (BMR * 1.8).roundToTwoDecimalPlaces()
                "Very Heavy Exercise" -> TDEE = (BMR * 2.0).roundToTwoDecimalPlaces()
            }

            var targetCalories = 0.0
            when (selectedTarget) {
                "Lose Weight" -> targetCalories = (TDEE * 0.75).roundToTwoDecimalPlaces()
                "Gain Weight" -> targetCalories = (TDEE * 1.1).roundToTwoDecimalPlaces()
                "Maintain Weight" -> targetCalories = TDEE.roundToTwoDecimalPlaces()
            }

            // Create a User object with the updated data
            val user = User(
                userID = auth.currentUser?.uid.toString(),
                email = auth.currentUser?.email.toString(), // Use the current user's email, fall back to the input email
                height = height,
                weight = weight,
                age = age,
                sex = sex.toString(),
                habit = habit.toString(),
                target = target.toString(),
                key = key, // Key might not be needed for updates, depending on your structure
                tdee = TDEE,
                targetCalories = targetCalories
            )

            // Update the user's data in Firebase
            uploadData(user)

            // Navigate to homeActivity after the update
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun uploadData(user: User) {
        // Update the user's profile in Firebase
        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(user.userID)
            .child("profile")
            .setValue(user)
            .addOnCompleteListener { task ->
                val loadingProgressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
                loadingProgressBar.visibility = View.GONE // Hide loading indicator

                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("UploadError", "Error updating data: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                val loadingProgressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
                loadingProgressBar.visibility = View.GONE // Hide loading indicator
            }}
    fun Double.roundToTwoDecimalPlaces(): Double {
        return String.format("%.2f", this).toDouble()
    }
    private fun fetchUserProfile(
        databaseReference: DatabaseReference,
        height: EditText,
        weight: EditText,
        age: EditText,
        habitRadio: RadioGroup,
        targetRadio: RadioGroup
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(User::class.java)
                userProfile?.let {
                    height.setText(it.height ) // Use setText() instead of direct assignment
                    weight.setText(it.weight)
                    age.setText(it.age)
                    sex = it.sex
                    key = it.key.toString()
                    // Handle RadioGroup selections for habitRadio and targetRadio
                    // Example: Select the appropriate radio button based on it.habit and it.target
                    when (it.habit) {
                        "No Exercise" -> habitRadio.check(R.id.noExercise)
                        "Light Exercise" -> habitRadio.check(R.id.lightExercise)
                        "Middle Exercise" -> habitRadio.check(R.id.middleExercise)
                        "Heavy Exercise" -> habitRadio.check(R.id.heavyExercise)
                        "Very Heavy Exercise" -> habitRadio.check(R.id.veryheavyExercise)
                    }
                    when (it.target) {
                        "Lose Weight" -> targetRadio.check(R.id.LoseWeight)
                        "Gain Weight" -> targetRadio.check(R.id.GainWeight)
                        "Maintain Weight" -> targetRadio.check(R.id.MaintainWeight)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }
}