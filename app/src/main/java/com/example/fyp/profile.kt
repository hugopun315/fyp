package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var profilePic: ImageView
    private lateinit var searchButtonS2: TextView
    private lateinit var userData: TextView
    private lateinit var height: TextView
    private lateinit var weight: TextView
    private lateinit var TDEE: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val viewTeam: LinearLayout = findViewById(R.id.viewTeam)
        val titles = arrayOf("Monthly Report", "Daily Record", "Modify Body Data")
        val subtitles = arrayOf("", "", "")
        val imageResources = intArrayOf(
            R.drawable.ic_report,
            R.drawable.ic_today,
            R.drawable.ic_edit
        )

        // Loop through the data and create list items
        titles.forEachIndexed { index, title ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.list_item_profile, viewTeam, false)

            val imageView: ImageView = itemView.findViewById(R.id.item_image)
            val titleText: TextView = itemView.findViewById(R.id.item_title)
            val subtitleText: TextView = itemView.findViewById(R.id.item_subtitle)

            imageView.setImageResource(imageResources[index])
            titleText.text = title
            subtitleText.text = subtitles[index]

            // Set an onClick listener for the itemView
            itemView.setOnClickListener {
                when (index) {
                    0 -> {
                        // Navigate to Monthly Report Activity
                        val intent = Intent(this,MonthlyReport::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        // Navigate to Daily Record Activity
                        val intent = Intent(this, DailyRecordActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {

                    }
                }
            }

            viewTeam.addView(itemView)
        }

        // Initialize views and set existing click listeners
        searchButtonS1 = findViewById(R.id.imageViewSearch)
        searchButtonS2 = findViewById(R.id.textViewSearch)
        homeButton1 = findViewById(R.id.imageView41)
        homeButton2 = findViewById(R.id.textView81)
        profilePic = findViewById(R.id.imageView3)
        userData = findViewById(R.id.textView8)
        height = findViewById(R.id.textView111)
        weight = findViewById(R.id.textView112)
        TDEE = findViewById(R.id.textView11)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")
        fetchUserProfile(databaseReference, userData, height, weight, TDEE)

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
    }

    private fun fetchUserProfile(
        databaseReference: DatabaseReference,
        userData: TextView,
        height: TextView,
        weight: TextView,
        tdee: TextView
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(User::class.java)
                userProfile?.let {
                    userData.text =
                        "Target: ${it.target}\nExercise Habit: ${it.habit}\nTarget Calories: ${it.targetCalories}"
                    height.text = "${it.height}cm"
                    weight.text = "${it.weight}kg"
                    tdee.text = "${it.tdee}cals"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }
}