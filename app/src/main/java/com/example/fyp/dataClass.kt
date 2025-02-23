package com.example.fyp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ChatService {
    @Headers("Content-Type: application/json", "api-key: c23867c4-235b-45d0-9557-ce8734e93d8c")
    @POST("deployments/gpt-4-o-mini/chat/completions/?api-version=2024-05-01-preview")
    @JvmSuppressWildcards
    fun sendMessage(@Body payload: Map<String, List<Message>>): Call<Map<String, Any>>
}

class ChatViewModel : ViewModel() {

    private val chatService: ChatService

    init {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://genai.hkbu.edu.hk/general/rest/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        chatService = retrofit.create(ChatService::class.java)
    }

    val messages = MutableLiveData<MutableList<Message>>().apply { value = mutableListOf() }

    fun sendMessage(content: String) {
        Log.d("ChatViewModel", "sendMessage called with content: $content")
        val userMessage = Message("user", content)
        val updatedMessages = messages.value ?: mutableListOf()
        updatedMessages.add(userMessage)
        messages.postValue(updatedMessages)

        val payload = mapOf("messages" to listOf(userMessage))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chatService.sendMessage(payload).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ChatViewModel", "Response body: $responseBody")
                    if (responseBody != null) {
                        val choices = responseBody["choices"] as? List<Map<String, Any>>
                        val firstChoice = choices?.firstOrNull()
                        val messageContent =
                            (firstChoice?.get("message") as? Map<String, Any>)?.get("content") as? String
                        if (messageContent != null) {
                            val botMessage = Message("bot", messageContent)
                            updatedMessages.add(botMessage)
                            messages.postValue(updatedMessages)
                            Log.d("ChatViewModel", "Message sent successfully: $botMessage")
                        } else {
                            Log.e("ChatViewModel", "Message content is null")
                        }
                    } else {
                        Log.e("ChatViewModel", "Response body is null")
                    }
                } else {
                    Log.e(
                        "ChatViewModel",
                        "Error sending message: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception sending message", e)
            }
        }
    }
}

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
    var key: String? = null,
    var brands: String = ""
)

data class User(
    val userID: String = "",
    val email: String = "",
    val height: String = "",
    val weight: String = "",
    val age: String = "",
    val sex: String = "",
    val habit: String = "",
    val target: String = "",
    var key: String? = null,
    var tdee: Double = 0.0,
    var targetCalories: Double = 0.0
)


data class FoodItem(
    val name: String,
    val calories: Int


)

data class Report(
    val carbohydrates: String = "",
     val protein : String = "" ,
    val fat : String = "",
    val record : String  = ""
)
data class FoodAPI(
    val product_name: String,
    val carbohydrates_100g: Double,
    val energy_kcal_100g: Double,
    val fat_100g: Double,
    val proteins_100g: Double,
    val image_url: String
)


data class Message(
    val role: String,
    val content: String
)

object UserProfileManager {
    var myProfile: User? = null
    var tdee: String? = null
    var caloriesConsumedToday: String? = null
    var proteinConsumedToday: String? = null
    var fatConsumedToday: String? = null
    var carbohydratesConsumedToday: String? = null
}