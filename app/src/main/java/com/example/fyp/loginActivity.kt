package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class loginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountTextView: TextView
    private lateinit var errorText: EditText
    private lateinit var  auth : FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.



        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
            finish()
        }








    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.inputEmail)
        passwordEditText = findViewById(R.id.inputPassword)
        loginButton = findViewById(R.id.btnLogin)
        createAccountTextView = findViewById(R.id.createAccount)
        errorText = findViewById(R.id.error)



        loginButton = findViewById(R.id.btnLogin)
        loginButton.setOnClickListener {
            var email = emailEditText.text.toString()
            var password = passwordEditText.text.toString()


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Login Success",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this,homeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }




        }

        createAccountTextView.setOnClickListener{
            val intent = Intent(this,createAccount::class.java)
            startActivity(intent)
            finish()
        }
    }
}