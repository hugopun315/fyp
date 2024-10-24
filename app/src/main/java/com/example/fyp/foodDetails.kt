package com.example.fyp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class foodDetails : AppCompatActivity() {
    private var key: String = ""
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)
        val weight: TextView = findViewById(R.id.weight)
        val title: TextView = findViewById(R.id.titleTextView)
        val image: ImageView = findViewById(R.id.foodPic)
        val cal: TextView = findViewById(R.id.Calories)
        val car: TextView = findViewById(R.id.carbohydrate)
        val pro: TextView = findViewById(R.id.protein)
        val fat: TextView = findViewById(R.id.fat)

        val bundle = intent.extras
        if (bundle != null) {
            title.text = bundle.getString("title") ?: "No Title"
            cal.text = "calories: " +bundle.getString("cal") + " kcal" ?: "0"
            car.text = "carbohydrates: " + bundle.getString("car") + " g"?: "0"
            pro.text = "protein: " + bundle.getString("pro") +  " g"?: "0"
            fat.text = "fat: " +bundle.getString("fat") + " g"?: "0"
            weight.text = "weight: " + bundle.getString("weight") + " g"?: "0"
            key = bundle.getString("key") ?: ""
            imageUrl = bundle.getString("image") ?: ""

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
    }
}