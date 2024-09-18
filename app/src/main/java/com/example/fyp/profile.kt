package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class profile : AppCompatActivity() {
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var searchButtonS1: ImageView
    private lateinit var searchButtonS2: TextView
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        searchButtonS1 = findViewById(R.id.imageViewSearch)
        searchButtonS2 = findViewById(R.id.textViewSearch)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)


        listView = findViewById(R.id.listView)

        searchButtonS1.setOnClickListener {
            val intent = Intent(this,firebaseDBSearch::class.java)
            startActivity(intent)
        }
        searchButtonS2.setOnClickListener {
            val intent = Intent(this,firebaseDBSearch::class.java)
            startActivity(intent)
        }

        homeButton1.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }

        val headerTextView = TextView(this)
        headerTextView.text = "Profile List"
        headerTextView.textSize = 20f
        headerTextView.setPadding(16, 16, 16, 16)
        // Add the TextView as a header to the ListView
        listView.addHeaderView(headerTextView)

        // Dummy data for the ListView
        val items = listOf(
            "Change body data",
            "Modify daily record",
            "Monthly report",
            "Modify favourite food"
        )

        // Create an ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        // Set the adapter to the ListView
        listView.adapter = adapter

    }
}