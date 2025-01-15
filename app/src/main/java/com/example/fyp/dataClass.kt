package com.example.fyp


data class Food(
    val name: String = "",
    val protein: String = "",
    val carbohydrates: String = "",
    val fat: String = "",
    val weight: String = "",
    val qty: String = "",
    val favour: String = "",
    val uri: String? = null,
    val calories: String = "",
    var key: String? = null
)

data class User(
    val userID : String = "",
        val email:String ="",
        val hight:String ="",
        val weight: String ="",
        val age :String ="",
        val sex :String ="",
        val habit:String ="",
        val targe:String ="",
        var key: String? = null,
        var tdee : Double =0.0
        )

data class FoodItem(
    val name: String,
    val calories: Int



)


data class FoodTesting(
    val product_name: String,
    val carbohydrates_100g: Double,
    val energy_kcal_100g: Double,
    val fat_100g: Double,
    val proteins_100g: Double,
    val image_url: String
)