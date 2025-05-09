package com.example.fyp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class foodDetails : AppCompatActivity() {
    private var key: String = ""
    private var imageUrl: String = ""
    private var fav: String = ""
    private var qty: String = ""
    private var foodTitle: String = ""
    private var foodWeight: String = ""
    private var foodCar: String = ""
    private var foodPro: String = ""
    private var foodFat: String = ""
    private var foodCal: String = ""
    private var newFoodCar: String = ""
    private var newFoodPro: String = ""
    private var newFoodFat: String = ""
    private var newFoodCal: String = ""
    private var newWeight: String = ""
    private var newQty: String = ""
    private var time: String = ""
    private var date: String = ""
    private var value: String = ""
    private var brands: String = ""
    private var food: Food? = null
    private var auth = FirebaseAuth.getInstance()
    private lateinit var decreaseQty: Button
    private lateinit var increaseQty: Button
    private lateinit var quantityEditText: EditText
    private lateinit var addButton: Button
    private lateinit var aiButton: Button
    val currentUser = auth.currentUser
    val userID = currentUser?.uid
    private var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)
        val weight: EditText = findViewById(R.id.weight)
        val title: TextView = findViewById(R.id.titleTextView)
        val image: ImageView = findViewById(R.id.foodPic)
        val cal: TextView = findViewById(R.id.Calories)
        val car: TextView = findViewById(R.id.carbohydrate)
        val pro: TextView = findViewById(R.id.protein)
        val fat: TextView = findViewById(R.id.fat)
        val brand: TextView = findViewById(R.id.brands)

        decreaseQty = findViewById(R.id.decreaseQty)
        increaseQty = findViewById(R.id.increaseQty)
        quantityEditText = findViewById(R.id.quantity)

        quantityEditText.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val input =
                (dest.toString() + source.toString()).replace("weight: ", "").replace("g", "")
                    .trim()
            if (input.isEmpty() || input.toInt() >= 1) {
                null // Accept the input
            } else {
                "" // Reject the input
            }
        })

        addButton = findViewById(R.id.addRecord)
        aiButton = findViewById(R.id.AIButton)
        decreaseQty.setOnClickListener {
            var currentQty = 0
            if (quantityEditText.text.toString() == "" || quantityEditText.text.toString() == null) {
                currentQty = 1
            } else {
                currentQty = quantityEditText.text.toString().toInt()
            }

            if (currentQty > 1) {
                quantityEditText.setText((currentQty - 1).toString())
            }


        }
        increaseQty.setOnClickListener {
            var currentQty = 0
            if (quantityEditText.text.toString() == "" || quantityEditText.text.toString() == null) {
                currentQty = 1
            } else {
                currentQty = quantityEditText.text.toString().toInt()
            }

            quantityEditText.setText((currentQty + 1).toString())
        }
        val bundle = intent.extras
        if (bundle != null) {
            title.text = bundle.getString("title") ?: "No Title"

            key = bundle.getString("key") ?: ""
            imageUrl = bundle.getString("image") ?: ""
            fav = bundle.getString("fav") ?: ""
            qty = bundle.getString("qty") ?: ""
            foodTitle = bundle.getString("title") ?: ""
            foodWeight = bundle.getString("weight") ?: ""
            foodCar = bundle.getString("car") ?: ""
            foodPro = bundle.getString("pro") ?: ""
            foodFat = bundle.getString("fat") ?: ""
            foodCal = bundle.getString("cal") ?: ""
            time = bundle.getString("time") ?: ""
            date = bundle.getString("date") ?: ""
            value = bundle.getString("value") ?: ""
            brands = bundle.getString("brands") ?: ""
            if (brands != "") {
                brand.text = "brand: " + brands
            }
            cal.text = "calories: " + foodCal + " kcal" ?: "0"
            car.text = "carbohydrates: " + foodCar + " g" ?: "0"
            pro.text = "protein: " + foodPro + " g" ?: "0"
            fat.text = "fat: " + foodFat + " g" ?: "0"
            weight.setText((bundle.getString("weight") ?: "0"))
            quantityEditText.setText(bundle.getString("qty") ?: "")
            if (value == "remove") {
                addButton.text = "remove"
                weight.isEnabled = false
                quantityEditText.isEnabled = false
                increaseQty.isEnabled = false
                decreaseQty.isEnabled = false
            } else if (value == "search") {
                addButton.visibility = View.INVISIBLE
            }
            // A for already
            // Load the image only if imageUrl is not empty
            if (imageUrl.isNotEmpty()) {
                Glide.with(this).load(imageUrl).into(image)
            } else {
                // Handle the case when there is no image URL
                image.setImageResource(R.drawable.apple_158989157) // Set a placeholder image
            }
        } else {
            // Handle the case when bundle is null
            Log.e("foodDetails", "Bundle is null")
        }



        addButton.setOnClickListener {
            if (addButton.text == "remove") {
                removeFood()
            } else {
                uploadFood()
            }

        }

        aiButton.setOnClickListener {


            val intent = Intent(context, chatGPTAPI::class.java).apply {
                putExtra("foodTitle", foodTitle)
                putExtra("foodCar", foodCar)
                putExtra("foodPro", foodPro)
                putExtra("foodFat", foodFat)
                putExtra("foodCal", foodCal)
                if(newQty == "" && newWeight ==""){
                    putExtra("weight", "100")
                }else{
                    if(newQty!= "" && newWeight !=""){
                        putExtra("weight", (newWeight.toDoubleOrNull()!! * 100.0 * newQty.toDoubleOrNull()!!).toString() )
                    }else if(newQty!= "" && newWeight ==""){
                        putExtra("weight", ( foodWeight.toDoubleOrNull()!! * newQty.toDoubleOrNull()!!).toString() )
                    }else if(newQty== "" && newWeight !=""){
                        putExtra("weight", ( newWeight.toDoubleOrNull()!! * 100.0 ).toString() )
                    }
                }
            }
            context.startActivity(intent)
        }
        weight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                var foodCalValue = foodCal.toDoubleOrNull() ?: 0.0
                var foodCarValue = foodCar.toDoubleOrNull() ?: 0.0
                var foodProValue = foodPro.toDoubleOrNull() ?: 0.0
                var foodFatValue = foodFat.toDoubleOrNull() ?: 0.0
                var weightValue = weight.text.toString().toDoubleOrNull()?.div(100.0) ?: 0.0
                newWeight = weightValue.toString()
                if (foodCalValue != null && foodCarValue != null && foodProValue != null && foodFatValue != null) {
                    foodCalValue = (foodCalValue * weightValue).roundToTwoDecimalPlaces()
                    foodCarValue = (foodCarValue * weightValue).roundToTwoDecimalPlaces()
                    foodProValue = (foodProValue * weightValue).roundToTwoDecimalPlaces()
                    foodFatValue = (foodFatValue * weightValue).roundToTwoDecimalPlaces()
                }


                if (newQty != "") {
                    newFoodCal = (foodCalValue * newQty.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                    newFoodCar = (foodCarValue * newQty.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                    newFoodPro = (foodProValue * newQty.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                    newFoodFat = (foodFatValue * newQty.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                } else {
                    newFoodCal = foodCalValue.roundToTwoDecimalPlaces().toString()
                    newFoodCar = foodCarValue.roundToTwoDecimalPlaces().toString()
                    newFoodPro = foodProValue.roundToTwoDecimalPlaces().toString()
                    newFoodFat = foodFatValue.roundToTwoDecimalPlaces().toString()
                }


                cal.text = "calories: " + newFoodCal + " kcal"
                car.text = "carbohydrates: " + newFoodCar + " g"
                pro.text = "protein: " + newFoodPro + " g"
                fat.text = "fat: " + newFoodFat + " g"

            }
        })

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                var foodCalValue = foodCal.toDoubleOrNull() ?: 0.0
                var foodCarValue = foodCar.toDoubleOrNull() ?: 0.0
                var foodProValue = foodPro.toDoubleOrNull() ?: 0.0
                var foodFatValue = foodFat.toDoubleOrNull() ?: 0.0
                if (quantityEditText.text.toString() == null || quantityEditText.text.toString() == "") {
                    newQty = "1"
                } else {
                    newQty = quantityEditText.text.toString()
                }


                var qtyValue = quantityEditText.text.toString().toDoubleOrNull() ?: 1.0
                foodCalValue = (foodCalValue * qtyValue).roundToTwoDecimalPlaces()
                foodCarValue = (foodCarValue * qtyValue).roundToTwoDecimalPlaces()
                foodProValue = (foodProValue * qtyValue).roundToTwoDecimalPlaces()
                foodFatValue = (foodFatValue * qtyValue).roundToTwoDecimalPlaces()


                if (newWeight != "") {
                    newFoodCal = (foodCalValue * newWeight.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                    newFoodCar = (foodCarValue * newWeight.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                    newFoodPro = (foodProValue * newWeight.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()
                    newFoodFat = (foodFatValue * newWeight.toDoubleOrNull()!!).roundToTwoDecimalPlaces().toString()

                } else {
                    newFoodCal = foodCalValue.roundToTwoDecimalPlaces().toString()
                    newFoodCar = foodCarValue.roundToTwoDecimalPlaces().toString()
                    newFoodPro = foodProValue.roundToTwoDecimalPlaces().toString()
                    newFoodFat = foodFatValue.roundToTwoDecimalPlaces().toString()
                }




                cal.text = "calories: " + newFoodCal + " kcal"
                car.text = "carbohydrates: " + newFoodCar + " g"
                pro.text = "protein: " + newFoodPro + " g"
                fat.text = "fat: " + newFoodFat + " g"

            }
        })


    }


    fun Double.roundToTwoDecimalPlaces(): Double {
        return String.format("%.2f", this).toDouble()
    }

    private fun uploadFood() {
        val name = foodTitle
        var cal = ""
        var protein = ""
        var carbohydrates = ""
        var fat = ""
        if (newFoodCal == "") {
            cal = foodCal
            protein = foodPro
            carbohydrates = foodCar
            fat = foodFat
        } else {
            cal = newFoodCal
            protein = newFoodPro
            carbohydrates = newFoodCar
            fat = newFoodFat

        }

        var weight = foodWeight
        if (newWeight != "" ) {
            weight = (newWeight.toDoubleOrNull()?.times(100.0)).toString()
        }
        var qty = qty
        if (newQty != "" ) {
            qty = newQty
        }

        val favour = "false"

        val imageURL = imageUrl
        val foodKey = key
        val date = date
        val time = time
        val brands = brands
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        // Create a Food object
        val dataClass = Food(
            name,
            protein,
            carbohydrates,
            fat,
            weight,
            qty,
            favour,
            imageURL,
            cal.toString(),
            foodKey,
            brands
        )

        Log.d("UploadData", "Data Class: $dataClass")

        // Use a unique key for each entry
        val key = FirebaseDatabase.getInstance().reference.child("Demo Food").push().key
        if (key != null) {
            if (userID != null) {
                val userMealsRef = FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(userID)
                    .child("meals")
                    .child(date)
                    .child(time)
                    .child("Food")
                    .child(name)

                userMealsRef.setValue(dataClass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                            // Update total calories
                            updateTotalCalories(userID, date, "+")
                        }
                    }.addOnFailureListener { e ->
                        Log.e("UploadError", "Error uploading data: ${e.message}")
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Failed to generate unique key", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFood() {
        val name = foodTitle
        val date = date
        val time = time
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        if (userID != null) {
            val userMealsRef = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(userID)
                .child("meals")
                .child(date)
                .child(time)
                .child("Food")
                .child(name)



            userMealsRef.removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
                        // Update total calories
                        updateTotalCalories(userID, date, "-")
                    }
                }.addOnFailureListener { e ->
                    Log.e("RemoveError", "Error removing data: ${e.message}")
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }


        }
    }

    private fun updateTotalCalories(userId: String, date: String, sign: String) {
        val mealsRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)
            .child("meals")
            .child(date)

        mealsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalCalories = 0.0
                var totalProtein = 0.0
                var totalFat = 0.0
                var totalCarbohydrates = 0.0
                for (mealSnapshot in dataSnapshot.children) {
                    if (mealSnapshot.key != "record") {
                        for (foodSnapshot in mealSnapshot.child("Food").children) {
                            val calories =
                                foodSnapshot.child("calories").getValue(String::class.java)
                                    ?.toDoubleOrNull() ?: 0.0
                            val protein = foodSnapshot.child("protein").getValue(String::class.java)
                                ?.toDoubleOrNull() ?: 0.0
                            val fat = foodSnapshot.child("fat").getValue(String::class.java)
                                ?.toDoubleOrNull() ?: 0.0
                            val carbohydrates =
                                foodSnapshot.child("carbohydrates").getValue(String::class.java)
                                    ?.toDoubleOrNull() ?: 0.0
                            totalCalories += calories
                            totalProtein += protein
                            totalFat += fat
                            totalCarbohydrates += carbohydrates
                        }
                    }
                }

                mealsRef.child("record").setValue(totalCalories.toString())
                mealsRef.child("protein").setValue(totalProtein.toString())
                mealsRef.child("fat").setValue(totalFat.toString())
                mealsRef.child("carbohydrates").setValue(totalCarbohydrates.toString())


                val dateParts = date.split("-")

                val year = dateParts[0]
                val month = dateParts[1]
                val day = dateParts[2]

                updateMonthReport(
                    userId,
                    year,
                    month,
                    day,
                    totalCalories.toString(),
                    totalProtein.toString(),
                    totalFat.toString(),
                    totalCarbohydrates.toString(),
                    sign
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data: ${databaseError.message}")
            }
        })

        val context: Context = this
        val intent = Intent(this, FindFoodView::class.java).apply {
            putExtra("time", time)
        }
        context.startActivity(intent)
        finish()

    }

    private fun updateMonthReport(
        userId: String,
        year: String,
        month: String,
        day: String,
        totalCalories: String,
        totalProtein: String,
        totalFat: String,
        totalCarbohydrates: String,
        sign: String
    ) {
        val monthRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)
            .child("MonthlyReport")
            .child(year)
            .child(month)

        val mealsRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)
            .child("meals")

        mealsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var record = 0.0
                var protein = 0.0
                var fat = 0.0
                var carbohydrates = 0.0

                for (mealSnapshot in dataSnapshot.children) {
                    val mealDate = mealSnapshot.key ?: continue
                    if (mealDate.startsWith("$year-$month")) {
                        for (timeSnapshot in mealSnapshot.children) {
                            for (foodSnapshot in timeSnapshot.child("Food").children) {
                                record += foodSnapshot.child("calories")
                                    .getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                                protein += foodSnapshot.child("protein")
                                    .getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                                fat += foodSnapshot.child("fat").getValue(String::class.java)
                                    ?.toDoubleOrNull() ?: 0.0
                                carbohydrates += foodSnapshot.child("carbohydrates")
                                    .getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                            }
                        }
                    }
                }

                Log.d(
                    "UpdateMonthReport",
                    "Recalculated values - Record: $record, Protein: $protein, Fat: $fat, Carbohydrates: $carbohydrates"
                )

                val updates = mapOf(
                    "record" to record.toString(),
                    "protein" to protein.toString(),
                    "fat" to fat.toString(),
                    "carbohydrates" to carbohydrates.toString(),
                    "$day/record" to totalCalories,
                    "$day/protein" to totalProtein,
                    "$day/fat" to totalFat,
                    "$day/carbohydrates" to totalCarbohydrates
                )

                monthRef.updateChildren(updates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UpdateMonthReport", "Values updated successfully")
                    } else {
                        Log.e(
                            "UpdateMonthReport",
                            "Failed to update values: ${task.exception?.message}"
                        )
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data: ${databaseError.message}")
            }
        })
    }
}