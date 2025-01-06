package com.example.fyp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
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
    private lateinit var eventListener: ValueEventListener // Declare the eventListener variable
    private lateinit var auth: FirebaseAuth
    private lateinit var TDEEView: TextView
    private lateinit var CCTView: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val todayDate = getCurrentDate()

        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")
        fetchUserTDEE(databaseReference)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("record")
        fetchUserCCT(databaseReference)

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
        TDEEView = findViewById(R.id.TDEE)
        CCTView = findViewById(R.id.CCT)

        // Set Layout Managers
        breakfastRecyclerView.layoutManager = LinearLayoutManager(this)
        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)

        val breakfastDataList = ArrayList<Food>()
        val lunchDataList = ArrayList<Food>()
        val breakfastAdapter = FoodAdapter(this, breakfastDataList, "", "")
        val lunchAdapter = FoodAdapter(this, lunchDataList, "", "")
        breakfastRecyclerView.adapter = breakfastAdapter
        lunchRecyclerView.adapter = lunchAdapter
        val dinnerDataList = ArrayList<Food>()
        val dinnerAdapter = FoodAdapter(this, dinnerDataList, "","")
        dinnerRecyclerView.adapter = dinnerAdapter

        // Fetch breakfast food items
        val breakfastDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("breakfast").child("Food")
        eventListener = object : ValueEventListener {
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
        breakfastDatabaseReference.addValueEventListener(eventListener) // Add the listener to the reference

        // Fetch lunch food items
        val lunchDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("lunch").child("Food")
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
        val dinnerDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("dinner").child("Food")
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

        // Set up click listeners for buttons
        searchButtonS1.setOnClickListener {
            val intent = Intent(this, firebaseDBSearch::class.java)
            startActivity(intent)
        }
        searchButtonS2.setOnClickListener {
            val intent = Intent(this, firebaseDBSearch::class.java)
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
        breakfastButton.setOnClickListener {
            val context: Context = this
            val intent = Intent(this, FindFoodView::class.java).apply {
                putExtra("time", "breakfast")
            }
            context.startActivity(intent)
        }

        lunchButton.setOnClickListener {
            val context: Context = this
            val intent = Intent(this, FindFoodView::class.java).apply {
                putExtra("time", "lunch")
            }
            context.startActivity(intent)
        }

        dinnerButton.setOnClickListener {
            val context: Context = this
            val intent = Intent(this, FindFoodView::class.java).apply {
                putExtra("time", "dinner")
            }
            context.startActivity(intent)
        }
    }

    private fun fetchUserTDEE(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                userProfile?.let {
                    TDEEView.text = "Your Total Daily Energy Expenditure : " + it.tdee.toString() + " kals "
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun fetchUserCCT(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val record = dataSnapshot.getValue(String::class.java)
                record?.let {
                    CCTView.text = "Calories Consumed today : $it kals"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}

data class UserProfile(
    val age: String? = null,
    val email: String? = null,
    val habit: String? = null,
    val height: String? = null,
    val key: String? = null,
    val sex: String? = null,
    val target: String? = null,
    val tdee: Double? = null,
    val userID: String? = null,
    val weight: String? = null
)