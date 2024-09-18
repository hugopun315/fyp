package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class loginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountTextView: TextView
    private lateinit var errorText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.inputEmail)
        passwordEditText = findViewById(R.id.inputPassword)
        loginButton = findViewById(R.id.btnLogin)
        createAccountTextView = findViewById(R.id.createAccount)
        errorText = findViewById(R.id.error)
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        */


        loginButton = findViewById(R.id.btnLogin)
        loginButton.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }

        createAccountTextView.setOnClickListener{
            val intent = Intent(this,createAccount::class.java)
            startActivity(intent)
        }
    }
}