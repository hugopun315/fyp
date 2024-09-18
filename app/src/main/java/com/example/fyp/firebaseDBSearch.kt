package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter

class firebaseDBSearch : AppCompatActivity() {
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var foodRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_firebase_dbsearch)


        foodRecyclerView = findViewById(R.id.fList)
       homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        val foodItems = listOf(
            FoodItem("Eggs", 150),
            FoodItem("Toast", 100),
            FoodItem("Orange Juice", 120)
        )

        foodRecyclerView.adapter = FoodAdapter(foodItems)

        homeButton1.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }






    }
}