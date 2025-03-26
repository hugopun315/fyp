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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class createAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var hightlEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var createButton: Button

    private lateinit var ageEditText: EditText

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sexRadio: RadioGroup
    private lateinit var habitRadio: RadioGroup
    private lateinit var targetRadio: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()
        sexRadio = findViewById(R.id.sex_list)
        habitRadio = findViewById(R.id.exercise_list)
        targetRadio = findViewById(R.id.targetList)
        var selectedSex: String? = null
        var selectedTarget: String? = null
        var selectedHabit: String? = null
        emailEditText = findViewById(R.id.inputEmail)
        passwordEditText = findViewById(R.id.inputPassword)
        hightlEditText = findViewById(R.id.inputHight)
        weightEditText = findViewById(R.id.inputWeight)

        ageEditText = findViewById(R.id.inputAge)

        createButton = findViewById(R.id.btnCreate)



        sexRadio.setOnCheckedChangeListener { group, checkedId ->
            selectedSex = when (checkedId) {
                R.id.radio_male -> "Male"
                R.id.radio_female -> "Female"
                else -> null
            }
            Toast.makeText(
                baseContext,
                selectedSex,
                Toast.LENGTH_SHORT,
            ).show()

        }

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




        createButton.setOnClickListener {
            val loadingProgressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
            loadingProgressBar.visibility = View.VISIBLE // Show loading indicator
            /*
            val intent = Intent(this,loginActivity::class.java)
            startActivity(intent)
*/

            var email = emailEditText.text.toString()
            var password = passwordEditText.text.toString()
            var height = hightlEditText.text.toString()
            var weight = weightEditText.text.toString()
            var age = ageEditText.text.toString()
            var sex = selectedSex
            var target = selectedTarget
            var habit = selectedHabit
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Account created.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        var BMR =0.0
                        if(sex == "Male"){
                            BMR = (weight.toDoubleOrNull()!! * 10 + 6.25 * height.toDoubleOrNull()!! - 5* age.toDoubleOrNull()!! +5)
                        }else {
                            BMR = (weight.toDoubleOrNull()!! * 10 + 6.25 * height.toDoubleOrNull()!! - 5* age.toDoubleOrNull()!! -161)
                        }
                        var TDEE = 0.0
                        when (selectedHabit){
                            "No Exercise" -> TDEE = (BMR * 1.2).roundToTwoDecimalPlaces()
                            "Light Exercise" -> TDEE = (BMR * 1.4).roundToTwoDecimalPlaces()
                            "Middle Exercise" -> TDEE = (BMR * 1.6).roundToTwoDecimalPlaces()
                            "Heavy Exercise" -> TDEE =  (BMR * 1.8).roundToTwoDecimalPlaces()
                            "Very Heavy Exercise" -> TDEE = (BMR * 2.0).roundToTwoDecimalPlaces()
                        }
                        var targetCalories = 0.0
                        when (selectedTarget){
                            "Lose Weight"-> targetCalories = (TDEE * 0.75).roundToTwoDecimalPlaces()
                            "Gain Weight" ->  targetCalories = (TDEE * 1.1).roundToTwoDecimalPlaces()
                            "Maintain Weight" -> targetCalories = (TDEE).roundToTwoDecimalPlaces()
                        }


                        val user = User(auth.currentUser?.uid.toString(), email, height, weight, age, sex.toString(), habit.toString(), target.toString(), "", TDEE , targetCalories)
                       uploadData(user)
                        val intent = Intent(this, homeActivity::class.java)
                        startActivity(intent)


                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }


        }

    }

    fun Double.roundToTwoDecimalPlaces(): Double {
        return String.format("%.2f", this).toDouble()
    }

    private fun uploadData(user : User) {


        // Create a Food object
        val dataClass : User = user

        Log.d("UploadData", "Data Class: $dataClass")

        // Use a unique key for each entry
        val key = FirebaseDatabase.getInstance().reference.child("Users").push().key
        if (key != null) {
            dataClass.key = key
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(user.userID)
                .child("profile")
                .setValue(dataClass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User Saved", Toast.LENGTH_SHORT).show()
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