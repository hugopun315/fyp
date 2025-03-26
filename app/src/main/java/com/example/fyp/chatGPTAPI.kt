package com.example.fyp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp.adapter.MessageAdapter
import com.example.fyp.databinding.ActivityChatGptapiBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class chatGPTAPI : AppCompatActivity() {
    val profile = UserProfileManager.myProfile
    private lateinit var binding: ActivityChatGptapiBinding
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityChatGptapiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        if (bundle != null) {
            val foodTitle = bundle.getString("foodTitle") ?: "null"
            val foodCar = bundle.getString("foodCar") ?: "null"
            val foodPro = bundle.getString("foodPro") ?: "null"
            val foodFat = bundle.getString("foodFat") ?: "null"
            val foodCal = bundle.getString("foodCal") ?: "null"
            val weight = bundle.getString("weight") ?: "null"
            askForComment(foodTitle, foodCar, foodPro, foodFat, foodCal, weight,  binding, viewModel)
        }

        val calories = UserProfileManager.caloriesConsumedToday
        val protein = UserProfileManager.proteinConsumedToday
        val fat = UserProfileManager.fatConsumedToday
        val carbohydrates = UserProfileManager.carbohydratesConsumedToday

        Log.d("chatGPTAPI", "onCreate called")

        val adapter = MessageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.messages.observe(this, Observer { messages ->
            Log.d("chatGPTAPI", "Messages updated: $messages")
            adapter.submitList(messages)
        })

        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            Log.d("chatGPTAPI", "Send button clicked with message: $message")
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.messageInput.text.clear()
            }
        }

        binding.presetButton1.setOnClickListener {
            var message =
                "my targetCalories is ${profile?.targetCalories} and Current time is ${getCurrentTime()} and I want to ${profile?.target}, "
            if (calories != "0.0" && calories != null) {
                message += "I have already Consumed $calories kals, $protein g protein, $fat g fat and $carbohydrates g carbohydrates, help me to design a menu to achieve the target based on the current time period. Reply in short."
            } else {
                message += "Help me to design a menu to achieve the target based on the current time period. Reply in short"
            }
            viewModel.sendMessage(message)
            binding.messageInput.text.clear()
        }
    }
}

fun askForComment(
    title: String,
    carbohydrates: String,
    protein: String,
    fat: String,
    foodCalories: String,
    weight : String,
    binding: ActivityChatGptapiBinding,
    viewModel: ChatViewModel
) {

    var message =
        "Here are the information of the food $title for 100g, carbohydrates: $carbohydrates, fat: $fat, protein: $protein, calories: $foodCalories. I want to intake for $weight grams. My targetCalories is ${ UserProfileManager.myProfile?.targetCalories}, and I want to ${ UserProfileManager.myProfile?.target}. "
    if (    UserProfileManager.caloriesConsumedToday!= "0.0" && UserProfileManager.caloriesConsumedToday!=null) {
        Log.d(TAG, "This is the number adfafsafds ${UserProfileManager.caloriesConsumedToday}")
        message += "And I have already Consumed ${UserProfileManager.caloriesConsumedToday} kals, ${UserProfileManager.proteinConsumedToday} g protein, ${UserProfileManager.fatConsumedToday} g fat and ${UserProfileManager.carbohydratesConsumedToday} g carbohydrates."
    }
        message +=" Give me a comment, should I intake this food? Reply in short"

    viewModel.sendMessage(message)
    binding.messageInput.text.clear()
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("GMT+8")
    val date = Date()
    return dateFormat.format(date)
}