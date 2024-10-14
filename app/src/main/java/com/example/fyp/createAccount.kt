package com.example.fyp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class createAccount : AppCompatActivity() {

    private lateinit var  auth : FirebaseAuth

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
        var selectedSex: String? = null
        var selectedTarget: String? = null
        var selectedHabit: String? = null
        emailEditText = findViewById(R.id.inputEmail)
        passwordEditText = findViewById(R.id.inputPassword)
        hightlEditText = findViewById(R.id.inputHight)
        weightEditText = findViewById(R.id.inputWeight)

        ageEditText = findViewById(R.id.inputAge)

        createButton = findViewById(R.id.btnCreate)


/*
        sexRadio.setOnCheckedChangeListener { group, checkedId ->
            selectedSex = when (checkedId) {
                R.id.radio_male -> "Male"
                R.id.radio_female -> "Female"
                else -> null
            }
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
        }

        targetRadio.setOnCheckedChangeListener { group, checkedId ->
            selectedTarget = when (checkedId) {
                R.id.LoseWeight -> "Lose Weight"
                R.id.GainWeight -> "Gain Weight"
                R.id.MaintainWeight -> "Maintain Weight"
                else -> null
            }
        }

        */

        createButton.setOnClickListener {
            /*
            val intent = Intent(this,loginActivity::class.java)
            startActivity(intent)
*/

            var  email = emailEditText.text.toString()
            var password = passwordEditText.text.toString()
            var hight = hightlEditText.text.toString()
            var weight = weightEditText.text.toString()
            var age = ageEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Account created.",
                            Toast.LENGTH_SHORT,
                        ).show()

                        val intent = Intent(this,homeActivity::class.java)
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


}