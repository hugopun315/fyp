package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FindFoodView : AppCompatActivity() {
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var eventListener: ValueEventListener // Declare the eventListener variable
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_food_view)
        foodRecyclerView = findViewById(R.id.foodList)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        val bundle = intent.extras
        val time = bundle?.getString("time") ?: ""
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = ArrayList<Food>()
        val adapter = FoodAdapter(this, dataList,time, "" )
        foodRecyclerView.adapter = adapter
        val databaseReference = FirebaseDatabase.getInstance().getReference("Demo Food")
        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Food::class.java)
                    dataClass?.let {
                        it.key = itemSnapshot.key
                        dataList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message) // Handle possible errors
            }
        }
        databaseReference.addValueEventListener(eventListener) // Add the listener to the reference



        homeButton1.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this,homeActivity::class.java)
            startActivity(intent)
        }

        profileButtonP1.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
        profileButtonP2.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

    }
}