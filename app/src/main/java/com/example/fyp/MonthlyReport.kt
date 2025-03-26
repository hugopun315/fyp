package com.example.fyp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class MonthlyReport : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var concludeTitle: TextView

    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private var protein: Double = 0.0
    private var fat: Double = 0.0
    private var carbohydrates: Double = 0.0
    private var record: Double = 0.0


    private var numbersOfDay =0
    private var avgCal =0.0
    private var userProfile: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)
        auth = FirebaseAuth.getInstance()
        concludeTitle = findViewById(R.id.concludeTitle)
        monthSpinner = findViewById(R.id.monthSpinner)
        yearSpinner = findViewById(R.id.yearSpinner)

        val currentUser = auth.currentUser

        val userID = currentUser?.uid

        setupSpinners()

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateData(userID)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateData(userID)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based, so add 1
    }
    private fun setupSpinners() {
        val months = arrayOf(""  ,"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val years = arrayOf("2025")

        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        monthSpinner.setSelection(getCurrentMonth()) // Default to February
        yearSpinner.setSelection(0) // Default to 2025
    }

    private fun updateData(userID: String?) {
        val selectedMonth = monthSpinner.selectedItem.toString()
        val selectedYear = yearSpinner.selectedItem.toString()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("MonthlyReport").child(selectedYear).child(selectedMonth)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val intake = findViewById<TextView>(R.id.intake)
        fetchPieChartData(databaseReference, pieChart, intake)

        databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("profile")
        fetchUserProfile(databaseReference2, object : UserProfileCallback {
            override fun onUserProfileFetched(userProfile: User?) {
                val lineChart = findViewById<LineChart>(R.id.lineChart)
                val details = findViewById<TextView>(R.id.details)
                fetchLineChartData(databaseReference, lineChart, userProfile, details)
            }
        })
    }

    private fun fetchPieChartData(databaseReference: DatabaseReference, pieChart: PieChart, intake: TextView) {
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
                    intake.text = "Total calorie intake: ${record.roundToTwoDecimalPlaces()}\nProtein: ${protein.roundToTwoDecimalPlaces()} g\nFat: ${fat.roundToTwoDecimalPlaces()} g\nCarbohydrates: ${carbohydrates.roundToTwoDecimalPlaces()} g\nThis pie chart shows the proportions of the three nutrients, including the total intake of protein, carbohydrates, and fat and their proportions."

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

                    // Custom ValueFormatter to add "%" symbol
                    pieDataSet.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.1f%%", value)
                        }
                    }

                    val pieData = PieData(pieDataSet)
                    pieChart.data = pieData
                    pieChart.setEntryLabelTextSize(12f)
                    pieChart.setEntryLabelColor(Color.BLACK) // Set entry label color to black
                    pieChart.invalidate()


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    fun Double.roundToTwoDecimalPlaces(): Double {
        return String.format("%.2f", this).toDouble()
    }



    private fun fetchLineChartData(databaseReference: DatabaseReference, lineChart: LineChart, userProfile: User?, details: TextView) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lineEntries = mutableListOf<Entry>()
                numbersOfDay = 0 // Reset count before calculation

                for (daySnapshot in dataSnapshot.children) {
                    val day = daySnapshot.key?.toFloatOrNull()
                    val record = daySnapshot.child("record").getValue(String::class.java)?.toFloatOrNull()
                    if (day != null && record != null) {
                        lineEntries.add(Entry(day, record))
                        numbersOfDay += 1
                    }
                }
                Log.e("before cal", avgCal.toString())
                // Calculate average calories
                val totalCalories = lineEntries.sumByDouble { it.y.toDouble() }
                val avgCal = (totalCalories / numbersOfDay).roundToTwoDecimalPlaces()
                Log.e("after cal", avgCal.toString())
                // Clear existing data and limit lines
                lineChart.clear()
                lineChart.axisLeft.removeAllLimitLines()

                // Create new data set
                val lineDataSet = LineDataSet(lineEntries, "Daily Calorie Intake")
                lineDataSet.color = ColorTemplate.COLORFUL_COLORS[0]
                lineDataSet.valueTextSize = 12f
                val lineData = LineData(lineDataSet)
                lineChart.data = lineData

                // Enable scaling and dragging
                lineChart.isDragEnabled = true
                lineChart.setScaleEnabled(true)
                lineChart.setPinchZoom(true)

                // Add a green limit line for average calories
                val avgLine = LimitLine(avgCal.toFloat(), "Avg")
                avgLine.lineWidth = 2f
                avgLine.lineColor = Color.GREEN
                avgLine.textColor = Color.GREEN
                avgLine.textSize = 12f

                // Add a red limit line for target calories
                val limitLine = LimitLine(userProfile?.targetCalories?.toFloat() ?: 2000f, "Target")
                limitLine.lineWidth = 2f
                limitLine.lineColor = Color.RED
                limitLine.textColor = Color.RED
                limitLine.textSize = 12f

                val yAxis = lineChart.axisLeft
                yAxis.addLimitLine(limitLine)
                yAxis.addLimitLine(avgLine)
                yAxis.axisMinimum = 0f // Start Y-axis at 0
                yAxis.setDrawLabels(true)
                yAxis.setDrawLimitLinesBehindData(true)
                yAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$value calories"
                    }
                }

                val xAxis = lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawLabels(true)
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "Day ${value.toInt()}"
                    }
                }

                lineChart.invalidate()

                if (userProfile != null) {
                    details.text = "The line chart shows the trend of daily intake, your average calories intake is $avgCal, and your calorie target is ${userProfile.targetCalories}."
                }

                // Call someOtherFunction after data is processed
                someOtherFunction(userProfile)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }



    private fun fetchUserProfile(databaseReference: DatabaseReference, callback: UserProfileCallback) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
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

                    // Update concludeTitle text here
                    callback.onUserProfileFetched(userProfile)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message) // Handle possible errors
            }
        })
    }

    private fun someOtherFunction(userProfile: User?) {

        // Access the stored values
        val viewModel: ChatViewModel by viewModels()
        val totalCalories = record
        val totalProtein = protein
        val totalFat = fat
        val totalCarbohydrates = carbohydrates

        val recommend = findViewById<TextView>(R.id.recommend)
        val avgCal = (totalCalories / numbersOfDay).roundToTwoDecimalPlaces()
        // Use userProfile data
        val targetCalories = userProfile?.targetCalories ?: 0.0
        val userTargetCal = userProfile?.targetCalories
        val userHeight = userProfile?.height ?: ""
        val userWeight = userProfile?.weight ?: ""
        val age = userProfile?.age ?: ""
        val target = userProfile?.target ?: ""
        val habit = userProfile?.habit ?: ""
        val sex = userProfile?.sex ?: ""
        Log.e("before message", avgCal.toString()) // Handle possible errors
        val sendText = "Now have $numbersOfDay day(s) recorded this month,my average Calories in this month is $avgCal and then my target calories is $userTargetCal in one day. Here are the details of my information :\nsex: $sex\nHeight: $userHeight\nWeight: $userWeight\nAge: $age\nTarget: To $target\nHabit: $habit In a week\nHere are the Total three nutrients intake (g) in $numbersOfDay day(s):\nProtein: $protein\nFat: $fat\nCarbohydrates: $carbohydrates\n" +
                "You have 2 tasks:\n" +
                "1. Summary: summarises the diet for the whole month, you can summary of objectives and data captured. but IN SHORT\n" +
                "2. Suggestions: provide some suggestions for future diets based on my goals and past diets,for example According to my information, recommend the optimal ratio of the three nutrients to be consumed. Also you can make suggestions based on user profile, goals and intake information But IN SHORT."

        Log.e("after message", avgCal.toString())
        // Send the message and update the recommend TextView with the AI response
        viewModel.sendAndDisplayMessage(sendText, recommend)


    }
}

interface UserProfileCallback {
    fun onUserProfileFetched(userProfile: User?)
}