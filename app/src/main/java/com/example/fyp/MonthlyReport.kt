package com.example.fyp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class MonthlyReport : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var concludeTitle: TextView
    private var protein: Double = 0.0
    private var fat: Double = 0.0
    private var carbohydrates: Double = 0.0
    private var record: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)
        auth = FirebaseAuth.getInstance()
        concludeTitle = findViewById(R.id.concludeTitle)
        val currentUser = auth.currentUser

        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("MonthlyReport").child("2025").child("02")
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        fetchPieChartData(databaseReference, pieChart)

        databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("profile")
        fetchUserProfile(databaseReference2)



    }

    private fun fetchPieChartData(databaseReference: DatabaseReference, pieChart: PieChart) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val report = dataSnapshot.getValue(Report::class.java)
                report?.let {
                    record = it.record.toDouble()
                    protein = it.protein.toDouble()
                    fat = it.fat.toDouble()
                    carbohydrates = it.carbohydrates.toDouble()

                    val chartProtein = protein * 4 / record * 100
                    val chartCarbohydrates = carbohydrates * 4 / record * 100
                    val chartFat = fat * 9 / record * 100

                    // Pie Chart setup
                    val pieEntries = listOf(
                        PieEntry(chartProtein.toFloat(), "Protein"),
                        PieEntry(chartCarbohydrates.toFloat(), "Carbohydrates"),
                        PieEntry(chartFat.toFloat(), "Fat")
                    )
                    val pieDataSet = PieDataSet(pieEntries, "Macronutrient Ratio")
                    pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
                    pieDataSet.valueTextSize = 18f
                    pieDataSet.valueTextColor = Color.BLACK
                    val pieData = PieData(pieDataSet)
                    pieChart.data = pieData
                    pieChart.setEntryLabelTextSize(12f)
                    pieChart.invalidate()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun fetchLineChartData(databaseReference: DatabaseReference, lineChart: LineChart, userProfile: User?) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lineEntries = mutableListOf<Entry>()
                for (daySnapshot in dataSnapshot.children) {
                    val day = daySnapshot.key?.toFloatOrNull()
                    val record = daySnapshot.child("record").getValue(String::class.java)?.toFloatOrNull()
                    if (day != null && record != null) {
                        lineEntries.add(Entry(day, record))
                    }
                }

                val lineDataSet = LineDataSet(lineEntries, "Daily Calorie Intake")
                lineDataSet.color = ColorTemplate.COLORFUL_COLORS[0]
                lineDataSet.valueTextSize = 12f
                val lineData = LineData(lineDataSet)
                lineChart.data = lineData

                // Enable scaling and dragging
                lineChart.isDragEnabled = true
                lineChart.setScaleEnabled(true)
                lineChart.setPinchZoom(true)

                // Add a red limit line at Y-axis 2000
                val limitLine = LimitLine(userProfile?.targetCalories?.toFloat()!!, "Target")
                limitLine.lineWidth = 2f
                limitLine.lineColor = Color.RED
                limitLine.textColor = Color.RED
                limitLine.textSize = 12f

                val yAxis = lineChart.axisLeft
                yAxis.addLimitLine(limitLine)

                lineChart.invalidate()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun fetchUserProfile(databaseReference2: DatabaseReference) {
        var userProfile: User?
        databaseReference2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userProfile = dataSnapshot.getValue(User::class.java)
                userProfile?.let {
                    it.height = dataSnapshot.child("height").getValue(String::class.java) ?: ""

                    it.weight = dataSnapshot.child("weight").getValue(String::class.java) ?: ""
                    it.target = dataSnapshot.child("target").getValue(String::class.java) ?: ""
                    it.age = dataSnapshot.child("age").getValue(String::class.java) ?: ""
                    it.habit = dataSnapshot.child("habit").getValue(String::class.java) ?: ""
                    it.sex = dataSnapshot.child("sex").getValue(String::class.java) ?: ""
                    it.targetCalories = dataSnapshot.child("targetCalories").getValue(Double::class.java) ?: 0.0
                    it.tdee = dataSnapshot.child("tdee").getValue(Double::class.java) ?: 0.0
                }

                val lineChart = findViewById<LineChart>(R.id.lineChart)
                fetchLineChartData(databaseReference, lineChart , userProfile)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })


    }
}