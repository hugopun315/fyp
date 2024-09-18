package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter

class homeActivity : AppCompatActivity() {
    private lateinit var breakfastRecyclerView: RecyclerView
    private lateinit var lunchRecyclerView: RecyclerView
    private lateinit var dinnerRecyclerView: RecyclerView
    private lateinit var searchButton1: ImageView
    private lateinit var searchButton2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Initialize RecyclerViews
        breakfastRecyclerView = findViewById(R.id.bList)
        lunchRecyclerView = findViewById(R.id.lList)
        dinnerRecyclerView = findViewById(R.id.dList)
        searchButton1 = findViewById(R.id.imageViewSearch)
        searchButton2 = findViewById(R.id.textViewSearch)

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

        searchButton1.setOnClickListener {
            val intent = Intent(this,firebaseDBSearch::class.java)
            startActivity(intent)
        }
        searchButton2.setOnClickListener {
            val intent = Intent(this,firebaseDBSearch::class.java)
            startActivity(intent)
        }
    }
}