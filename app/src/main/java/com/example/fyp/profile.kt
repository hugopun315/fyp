package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
}