package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class createAccount : AppCompatActivity() {



    private lateinit var hightlEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var createButton: Button

    private lateinit var ageEditText: EditText

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        emailEditText = findViewById(R.id.inputEmail)
        passwordEditText = findViewById(R.id.inputPassword)
        hightlEditText = findViewById(R.id.inputHight)
        weightEditText = findViewById(R.id.inputWeight)

        ageEditText = findViewById(R.id.inputAge)

        createButton = findViewById(R.id.btnCreate)

        createButton.setOnClickListener {
            val intent = Intent(this,loginActivity::class.java)
            startActivity(intent)

        }

    }


}