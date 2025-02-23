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
    private lateinit var auth: FirebaseAuth
    private lateinit var concludeTitle : TextView
    private var protien: Double = 0.0
    private  var fat: Double  =0.0
    private  var carbohydrates: Double  =0.0
    private  var record: Double =0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)
        auth = FirebaseAuth.getInstance()
       concludeTitle = findViewById(R.id.concludeTitle)
        val currentUser = auth.currentUser

        val userID = currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("MonthlyReport").child("2025").child("02")
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        fetchUserProfile(databaseReference , pieChart)




        val lineChart = findViewById<LineChart>(R.id.lineChart)

        // Line Chart setup
        val lineEntries = listOf(
            Entry(1f, 2000f),
            Entry(2f, 1800f),
            Entry(3f, 2200f),
            Entry(4f, 2100f),
            Entry(5f, 2300f),
            Entry(6f, 2300f),
            Entry(7f, 2300f),
            Entry(8f, 2300f),
            Entry(9f, 2300f),
            Entry(10f, 2300f),
            Entry(11f, 2300f),
            Entry(12f, 2300f),
            Entry(13f, 2300f),
            Entry(14f, 2300f),
            // Add more entries for the entire month
        )
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
        val limitLine = LimitLine(2000f, "Target")
        limitLine.lineWidth = 2f
        limitLine.lineColor = Color.RED
        limitLine.textColor = Color.RED
        limitLine.textSize = 12f

        val yAxis = lineChart.axisLeft
        yAxis.addLimitLine(limitLine)

        lineChart.invalidate()
    }

    private fun fetchUserProfile(databaseReference: DatabaseReference , pieChart : PieChart) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val report  = dataSnapshot.getValue(Report::class.java)
               report?.let {
                   record = it.record.toDouble()

                   protien = it.protein.toDouble()

                   fat = it.fat.toDouble()
                   carbohydrates = it.carbohydrates.toDouble()

                   val  chartProtien = protien*4 / record *100
                   val     chartCarbohydrates = carbohydrates *4  / record *100
                   val    chartFat = (record - protien*4 - carbohydrates *4) /record *100


                   // Pie Chart setup
                   val pieEntries = listOf(
                       PieEntry(chartProtien.toFloat(), "Protein"),
                       PieEntry( chartCarbohydrates.toFloat(), "Carbohydrates"),
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
}