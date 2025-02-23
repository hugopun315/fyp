package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DailyRecordActivity : AppCompatActivity() {
    private lateinit var datePicker: DatePicker
    private lateinit var breakfastRecyclerView: RecyclerView
    private lateinit var lunchRecyclerView: RecyclerView
    private lateinit var dinnerRecyclerView: RecyclerView
    private lateinit var buttonRecord: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var searchButtonS1: ImageView
    private lateinit var searchButtonS2: TextView
    private lateinit var CCTView: TextView

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_record)


        breakfastRecyclerView = findViewById(R.id.bList)
        lunchRecyclerView = findViewById(R.id.lList)
        dinnerRecyclerView = findViewById(R.id.dList)
        datePicker = findViewById(R.id.datePicker)
        searchButtonS1 = findViewById(R.id.imageViewSearch)
        searchButtonS2 = findViewById(R.id.textViewSearch)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        CCTView = findViewById(R.id.CCT)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")

        buttonRecord = findViewById(R.id.viewRecordButton)

        breakfastRecyclerView.layoutManager = LinearLayoutManager(this)
        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)

        val breakfastDataList = ArrayList<Food>()
        val lunchDataList = ArrayList<Food>()
        val dinnerDataList = ArrayList<Food>()


        buttonRecord.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1 // Month is 0-based
            val year = datePicker.year
            val selectedDate = String.format("%04d-%02d-%02d", year, month, day)
            val breakfastAdapter = FoodAdapter(this, breakfastDataList, "breakfast", selectedDate, "remove")
            val lunchAdapter = FoodAdapter(this, lunchDataList, "lunch", selectedDate,"remove")
            val dinnerAdapter = FoodAdapter(this, dinnerDataList, "dinner", selectedDate,"remove")

            breakfastRecyclerView.adapter = breakfastAdapter
            lunchRecyclerView.adapter = lunchAdapter
            dinnerRecyclerView.adapter = dinnerAdapter

            if (userID != null) {

                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(selectedDate).child("record")
                fetchUserCCT(databaseReference ,selectedDate )
                // Fetch breakfast food items
                val breakfastDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(selectedDate).child("breakfast").child("Food")
                val breakfastEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        breakfastDataList.clear()
                        for (itemSnapshot in snapshot.children) {
                            val dataClass = itemSnapshot.getValue(Food::class.java)
                            dataClass?.let {
                                it.key = itemSnapshot.key
                                breakfastDataList.add(it)
                            }
                        }
                        breakfastAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", error.message) // Handle possible errors
                    }
                }
                breakfastDatabaseReference.addValueEventListener(breakfastEventListener) // Add the listener to the reference

                // Fetch lunch food items
                val lunchDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(selectedDate).child("lunch").child("Food")
                val lunchEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        lunchDataList.clear()
                        for (itemSnapshot in snapshot.children) {
                            val dataClass = itemSnapshot.getValue(Food::class.java)
                            dataClass?.let {
                                it.key = itemSnapshot.key
                                lunchDataList.add(it)
                            }
                        }
                        lunchAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", error.message) // Handle possible errors
                    }
                }
                lunchDatabaseReference.addValueEventListener(lunchEventListener) // Add the listener to the reference

                // Fetch dinner food items
                val dinnerDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(selectedDate).child("dinner").child("Food")
                val dinnerEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dinnerDataList.clear()
                        for (itemSnapshot in snapshot.children) {
                            val dataClass = itemSnapshot.getValue(Food::class.java)
                            dataClass?.let {
                                it.key = itemSnapshot.key
                                dinnerDataList.add(it)
                            }
                        }
                        dinnerAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", error.message) // Handle possible errors
                    }
                }
                dinnerDatabaseReference.addValueEventListener(dinnerEventListener) // Add the listener to the reference
            }
        }

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

    private fun fetchUserCCT(databaseReference: DatabaseReference, selectedDate : String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val record = dataSnapshot.getValue(String::class.java)
                record?.let {
                    CCTView.text = "Calories Consumed at $selectedDate : $it kals"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }
}