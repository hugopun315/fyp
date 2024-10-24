package com.example.fyp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class firebaseDBSearch : AppCompatActivity() {
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        enableEdgeToEdge()
        setContentView(R.layout.activity_firebase_dbsearch)


        foodRecyclerView = findViewById(R.id.fList)
       homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
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


        profileButtonP1.setOnClickListener {
            val intent = Intent(this,profile::class.java)
            startActivity(intent)
        }
        profileButtonP2.setOnClickListener {
            val intent = Intent(this,profile::class.java)
            startActivity(intent)
        }

*/

    }
}