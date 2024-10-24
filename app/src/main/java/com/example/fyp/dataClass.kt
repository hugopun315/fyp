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

data class FoodItem(
    val name: String,
    val calories: Int



)