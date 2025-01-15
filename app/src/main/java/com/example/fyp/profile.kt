package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class profile : AppCompatActivity() {
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var searchButtonS1: ImageView
    private lateinit var searchButtonS2: TextView
    private lateinit var listView: ListView
    private lateinit var headerText: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        searchButtonS1 = findViewById(R.id.imageViewSearch)
        searchButtonS2 = findViewById(R.id.textViewSearch)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        listView = findViewById(R.id.listView)
        headerText = findViewById(R.id.headerTextView)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")
        fetchUserProfile(databaseReference)

        searchButtonS1.setOnClickListener {
            val intent = Intent(this, firebaseDBSearch::class.java)
            startActivity(intent)
        }
        searchButtonS2.setOnClickListener {
            val intent = Intent(this, firebaseDBSearch::class.java)
            startActivity(intent)
        }

        homeButton1.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }

        val headerTextView = TextView(this)
        headerTextView.text = "Profile List"
        headerTextView.textSize = 20f
        headerTextView.setPadding(16, 16, 16, 16)
        listView.addHeaderView(headerTextView)

        val items = listOf(
            "Change body data",
            "View daily record",
            "Monthly report",
            "Modify favourite food"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            // Adjust position for header view
            val adjustedPosition = position - listView.headerViewsCount
            when (adjustedPosition) {
                1 -> { // "View daily record" item
                    val intent = Intent(this, DailyRecordActivity::class.java)
                    startActivity(intent)
                }
                // Handle other items if needed
            }
        }
    }

    private fun fetchUserProfile(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile2::class.java)
                userProfile?.let {
                    headerText.text = "Your body data:\nHeight: ${it.hight} cm\nWeight: ${it.weight} KG\nTarget: ${it.targe}\nTDEE: ${it.tdee} kcal\n\nChoose a function below"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }
}

data class UserProfile2(
    val age: String? = null,
    val email: String? = null,
    val habit: String? = null,
    val hight: String? = null,
    val key: String? = null,
    val sex: String? = null,
    val targe: String? = null,
    val tdee: Double? = null,
    val userID: String? = null,
    val weight: String? = null
)