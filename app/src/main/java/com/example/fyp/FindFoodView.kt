package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FindFoodView : AppCompatActivity() {
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var eventListener: ValueEventListener
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    private lateinit var searchButton: Button
    private lateinit var searchBar: EditText
    private lateinit var addOwnFood: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_food_view)
        foodRecyclerView = findViewById(R.id.foodList)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        searchButton = findViewById(R.id.search_button)
        searchBar = findViewById(R.id.search_bar)
        addOwnFood = findViewById(R.id.addOwnFood)
        val todayDate = getCurrentDate()
        val bundle = intent.extras
        val time = bundle?.getString("time") ?: ""
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = ArrayList<Food>()
        val adapter = FoodAdapter(this, dataList, time, todayDate , "A")
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
        databaseReference.addValueEventListener(eventListener)

        searchButton.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchFood(query, adapter, dataList)
            }
        }

        homeButton1.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
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

        addOwnFood.setOnClickListener {
            val intent = Intent(this, uploadFood::class.java)
            startActivity(intent)
        }
    }

    private fun searchFood(query: String, adapter: FoodAdapter, dataList: ArrayList<Food>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Demo Food")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Food::class.java)
                    dataClass?.let {
                        if (it.name.contains(query, ignoreCase = true)) {
                            it.key = itemSnapshot.key
                            dataList.add(it)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message) // Handle possible errors
            }
        })
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}