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
    private lateinit var aiButtonS1: ImageView
    private lateinit var aiButtonS2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    private lateinit var breakfastButton: Button
    private lateinit var lunchButton: Button
    private lateinit var dinnerButton: Button
    private lateinit var eventListener: ValueEventListener // Declare the eventListener variable
    private lateinit var auth: FirebaseAuth
    private lateinit var TDEEView: TextView
    private lateinit var CCTView: TextView
    private lateinit var CPView: TextView
    private lateinit var CFView: TextView
    private lateinit var CCView: TextView
    private lateinit var targetView: TextView
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val todayDate = getCurrentDate()
        val year = getCurrentYear()
        val month = getCurrentMonth()
        val day = getCurrentDay()
        val userID = currentUser?.uid




        // Initialize RecyclerViews
        breakfastRecyclerView = findViewById(R.id.bList)
        lunchRecyclerView = findViewById(R.id.lList)
        dinnerRecyclerView = findViewById(R.id.dList)
        aiButtonS1 = findViewById(R.id.imageViewAI)
        aiButtonS2 = findViewById(R.id.textViewAI)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        breakfastButton = findViewById(R.id.bButton)
        lunchButton = findViewById(R.id.lButton)
        dinnerButton = findViewById(R.id.dButton)
       targetView = findViewById(R.id.targetInput_input)
        CCTView = findViewById(R.id.calories_input)
        TDEEView = findViewById(R.id.targetCalorie_input)
        CPView = findViewById(R.id.protein_input)
        CFView = findViewById(R.id.fat_input)
        CCView = findViewById(R.id.carbs_input)
        // Set Layout Managers
        breakfastRecyclerView.layoutManager = LinearLayoutManager(this)
        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)



        getData(userID!!, todayDate)
        getFoodItems(userID!! , todayDate)
        // Set up click listeners for buttons
        aiButtonS1.setOnClickListener {
            val intent = Intent(this, chatGPTAPI::class.java)
            startActivity(intent)

        }
        aiButtonS2.setOnClickListener {
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

    fun getData(userID: String , todayDate: String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("profile")
        fetchUserTarget(databaseReference)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("record")
        fetchUserCCT(databaseReference)


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("protein")
        fetchUserCProtein(databaseReference)


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("fat")
        fetchUserCFat(databaseReference)


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("meals").child(todayDate).child("carbohydrates")
        fetchUserCCarbohydrates(databaseReference)
    }

    fun getFoodItems(userID: String , todayDate: String){
        val breakfastDataList = ArrayList<Food>()
        val lunchDataList = ArrayList<Food>()
        val breakfastAdapter = FoodAdapter(this, breakfastDataList, "breakfast", todayDate , "remove")
        val lunchAdapter = FoodAdapter(this, lunchDataList, "lunch", todayDate, "remove")
        breakfastRecyclerView.adapter = breakfastAdapter
        lunchRecyclerView.adapter = lunchAdapter
        val dinnerDataList = ArrayList<Food>()
        val dinnerAdapter = FoodAdapter(this, dinnerDataList, "dinner",todayDate, "remove")
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

    }

    override fun onRestart() {
        super.onRestart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val todayDate = getCurrentDate()
        val year = getCurrentYear()
        val month = getCurrentMonth()
        val day = getCurrentDay()
        val userID = currentUser?.uid

        getData(userID!!, todayDate)
        getFoodItems(userID!!, todayDate)
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val todayDate = getCurrentDate()
        val year = getCurrentYear()
        val month = getCurrentMonth()
        val day = getCurrentDay()
        val userID = currentUser?.uid

        getData(userID!!, todayDate)
        getFoodItems(userID!!, todayDate)
    }
    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val todayDate = getCurrentDate()
        val year = getCurrentYear()
        val month = getCurrentMonth()
        val day = getCurrentDay()
        val userID = currentUser?.uid

        getData(userID!!, todayDate)
        getFoodItems(userID!!, todayDate)
    }

    private fun fetchUserTarget(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(User::class.java)

                userProfile?.let {
                    UserProfileManager.myProfile = userProfile
                    val profile = UserProfileManager.myProfile
                    UserProfileManager.targetCalories = profile?.targetCalories!!
                    TDEEView.text = UserProfileManager.targetCalories.toString()
                    UserProfileManager.target = profile?.target!!
                    targetView.text=   "Target: " +UserProfileManager.target
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
                    UserProfileManager.caloriesConsumedToday = it
                    CCTView.text = "$it kals"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun fetchUserCProtein(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val protein = dataSnapshot.getValue(String::class.java)
                protein?.let {
                    UserProfileManager.proteinConsumedToday = it
                    CPView.text = "$it g"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun fetchUserCFat(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val record = dataSnapshot.getValue(String::class.java)
                record?.let {
                    UserProfileManager.fatConsumedToday = it
                    CFView.text = "$it g"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun fetchUserCCarbohydrates(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val record = dataSnapshot.getValue(String::class.java)
                record?.let {
                    UserProfileManager.carbohydratesConsumedToday = it
                    CCView.text = "$it g"
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

    fun getCurrentYear(): String {
        val calendar = Calendar.getInstance()
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return yearFormat.format(calendar.time)
    }

    fun getCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
        return monthFormat.format(calendar.time)
    }

    fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dayFormat.format(calendar.time)
    }
}
