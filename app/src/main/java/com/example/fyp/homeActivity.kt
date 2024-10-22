package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter

class homeActivity : AppCompatActivity() {
    private lateinit var breakfastRecyclerView: RecyclerView
    private lateinit var lunchRecyclerView: RecyclerView
    private lateinit var dinnerRecyclerView: RecyclerView
    private lateinit var searchButtonS1: ImageView
    private lateinit var searchButtonS2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    private lateinit var breakfastButton: Button
    private lateinit var lunchButton: Button
    private lateinit var dinnerButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Initialize RecyclerViews
        breakfastRecyclerView = findViewById(R.id.bList)
        lunchRecyclerView = findViewById(R.id.lList)
        dinnerRecyclerView = findViewById(R.id.dList)
        searchButtonS1 = findViewById(R.id.imageViewSearch)
        searchButtonS2 = findViewById(R.id.textViewSearch)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        breakfastButton = findViewById(R.id.bButton)
        lunchButton = findViewById(R.id.lButton)
        dinnerButton = findViewById(R.id.dButton)
        // Set Layout Managers
        breakfastRecyclerView.layoutManager = LinearLayoutManager(this)
        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create dummy data
        val breakfastItems = listOf(
            FoodItem("Eggs", 150),
            FoodItem("Toast", 100),
            FoodItem("Orange Juice", 120)
        )

        val lunchItems = listOf(
            FoodItem("Chicken Salad", 350),
            FoodItem("Pasta", 400),
            FoodItem("Soda", 150),
            FoodItem("Eggs", 150),
            FoodItem("Toast", 100)

        )

        val dinnerItems = listOf(
            FoodItem("Steak", 500),
            FoodItem("Mashed Potatoes", 300),
            FoodItem("Green Beans", 80)
        )

        // Set adapters to RecyclerViews

        breakfastRecyclerView.adapter = FoodAdapter(breakfastItems)
        lunchRecyclerView.adapter = FoodAdapter(lunchItems)
        dinnerRecyclerView.adapter = FoodAdapter(dinnerItems)

        searchButtonS1.setOnClickListener {
            val intent = Intent(this,firebaseDBSearch::class.java)
            startActivity(intent)
        }
        searchButtonS2.setOnClickListener {
            val intent = Intent(this,firebaseDBSearch::class.java)
            startActivity(intent)
        }

        profileButtonP1.setOnClickListener {
            val intent = Intent(this,profile::class.java)
            startActivity(intent)
        }
        profileButtonP2.setOnClickListener {
            val intent = Intent(this,profile::class.java)
            startActivity(intent)
        }

        breakfastButton.setOnClickListener {  val intent = Intent(this,uploadFood::class.java)
            startActivity(intent)
            finish() }
    }
}