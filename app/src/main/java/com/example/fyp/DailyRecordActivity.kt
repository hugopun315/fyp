package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
class DailyRecordActivity : AppCompatActivity() {
    private lateinit var datePicker: DatePicker
    private lateinit var breakfastRecyclerView: RecyclerView
    private lateinit var lunchRecyclerView: RecyclerView
    private lateinit var dinnerRecyclerView: RecyclerView
    private lateinit var alertText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var aiButton1: ImageView
    private lateinit var aiButton2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    private lateinit var CCTView: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_record)

        alertText = findViewById(R.id.viewRecordButton)
        breakfastRecyclerView = findViewById(R.id.bList)
        lunchRecyclerView = findViewById(R.id.lList)
        dinnerRecyclerView = findViewById(R.id.dList)
        datePicker = findViewById(R.id.datePicker)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        aiButton1 = findViewById(R.id.imageViewAI)
        aiButton2 = findViewById(R.id.textViewAI)
        CCTView = findViewById(R.id.CCT)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")

        breakfastRecyclerView.layoutManager = LinearLayoutManager(this)
        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the DatePicker with the current date and set the listener
        val calendar = Calendar.getInstance()
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            // This lambda is called whenever the date is changed
            val selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
            if (userID != null) {
                getData(userID, selectedDate)
            }
        }

        // Load data for the current date initially
        if (userID != null) {
            getData(userID, getCurrentDate())
        }


                homeButton1.setOnClickListener {
                    val intent = Intent(this, homeActivity::class.java)
                    startActivity(intent)
                }
        homeButton2.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }

        aiButton1.setOnClickListener {
            val intent = Intent(this, chatGPTAPI::class.java)
            startActivity(intent)
        }
        aiButton2.setOnClickListener {
            val intent = Intent(this, chatGPTAPI::class.java)
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

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getData(userID: String, today: String) {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Month is 0-based
        val year = datePicker.year

        val breakfastDataList = ArrayList<Food>()
        val lunchDataList = ArrayList<Food>()
        val dinnerDataList = ArrayList<Food>()
        val selectedDate: String
        if (today != "") {
            selectedDate = today
        } else {
            selectedDate = String.format("%04d-%02d-%02d", year, month, day)
        }

        val breakfastAdapter = FoodAdapter(this, breakfastDataList, "breakfast", selectedDate, "remove")
        val lunchAdapter = FoodAdapter(this, lunchDataList, "lunch", selectedDate, "remove")
        val dinnerAdapter = FoodAdapter(this, dinnerDataList, "dinner", selectedDate, "remove")

        breakfastRecyclerView.adapter = breakfastAdapter
        lunchRecyclerView.adapter = lunchAdapter
        dinnerRecyclerView.adapter = dinnerAdapter

        // Counter to track when all meal data has been fetched
        var fetchCount = 0
        val totalFetches = 3 // Breakfast, Lunch, Dinner

        // Function to check if all data is fetched and update alertText visibility
        fun checkAndUpdateAlertText() {
            fetchCount++
            if (fetchCount == totalFetches) {
                // All data has been fetched, check if there's any data
                val hasData = breakfastDataList.isNotEmpty() || lunchDataList.isNotEmpty() || dinnerDataList.isNotEmpty()
                alertText.visibility = if (hasData) View.GONE else View.VISIBLE
            }
        }

        if (userID != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(selectedDate).child("record")
            fetchUserCCT(databaseReference, selectedDate)

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
                    checkAndUpdateAlertText()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", error.message)
                    checkAndUpdateAlertText() // Still increment the counter to avoid hanging
                }
            }
            breakfastDatabaseReference.addValueEventListener(breakfastEventListener)

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
                    checkAndUpdateAlertText()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", error.message)
                    checkAndUpdateAlertText()
                }
            }
            lunchDatabaseReference.addValueEventListener(lunchEventListener)

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
                    checkAndUpdateAlertText()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", error.message)
                    checkAndUpdateAlertText()
                }
            }
            dinnerDatabaseReference.addValueEventListener(dinnerEventListener)
        }
    }

    private fun fetchUserCCT(databaseReference: DatabaseReference, selectedDate: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val record = dataSnapshot.getValue(String::class.java)
                record?.let {
                    CCTView.text = "Calories Consumed at $selectedDate: $it kals"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message)
            }
        })
    }
}