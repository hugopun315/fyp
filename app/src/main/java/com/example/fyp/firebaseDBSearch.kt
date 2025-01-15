package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class firebaseDBSearch : AppCompatActivity() {
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    private lateinit var searchButton: Button
    private lateinit var searchBar: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_dbsearch)

        foodRecyclerView = findViewById(R.id.foodList)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        searchButton = findViewById(R.id.search_button)
        searchBar = findViewById(R.id.search_bar)

        val todayDate = getCurrentDate()
        val bundle = intent.extras
        val time = bundle?.getString("time") ?: ""

        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = ArrayList<Food>()
        val adapter = FoodAdapter(this, dataList, time, todayDate, "search")
        foodRecyclerView.adapter = adapter

        fetchFoodData(adapter, dataList)

        searchButton.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchFood(query, adapter, dataList)
            }
        }

        homeButton1.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
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


    }

    private fun fetchFoodData(adapter: FoodAdapter, dataList: ArrayList<Food>) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://world.openfoodfacts.org/api/v2/search?categories_tags_en=chicken%20breast&energy-kj_100g%3C500&fields=product_name,carbohydrates_100g,energy-kcal_100g,fat_100g,proteins_100g,image_url,code")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData)
                    val productsArray = jsonObject.getJSONArray("products")

                    for (i in 0 until productsArray.length()) {
                        val productObject = productsArray.getJSONObject(i)
                        val foodItem = Food(
                            name = if (productObject.has("product_name")) productObject.getString("product_name") else "",
                            carbohydrates = if (productObject.has("carbohydrates_100g")) productObject.getDouble("carbohydrates_100g").toString() else "",
                            calories = if (productObject.has("energy-kcal_100g")) productObject.getDouble("energy-kcal_100g").toString() else "",
                            fat = if (productObject.has("fat_100g")) productObject.getDouble("fat_100g").toString() else "",
                            protein = if (productObject.has("proteins_100g")) productObject.getDouble("proteins_100g").toString() else "",
                            uri = if (productObject.has("image_url")) productObject.getString("image_url") else "",
                            weight = "100",
                            qty = "1",
                            favour = "",
                            key = if (productObject.has("code")) productObject.getString("code") else ""
                        )
                        dataList.add(foodItem)
                    }

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun searchFood(query: String, adapter: FoodAdapter, dataList: ArrayList<Food>) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://world.openfoodfacts.org/api/v2/search?categories_tags_en=$query&fields=product_name,carbohydrates_100g,energy-kcal_100g,fat_100g,proteins_100g,image_url,code")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData)
                    val productsArray = jsonObject.getJSONArray("products")

                    dataList.clear()
                    for (i in 0 until productsArray.length()) {
                        val productObject = productsArray.getJSONObject(i)
                        val foodItem = Food(
                            name = if (productObject.has("product_name")) productObject.getString("product_name") else "",
                            carbohydrates = if (productObject.has("carbohydrates_100g")) productObject.getDouble("carbohydrates_100g").toString() else "",
                            calories = if (productObject.has("energy-kcal_100g")) productObject.getDouble("energy-kcal_100g").toString() else "",
                            fat = if (productObject.has("fat_100g")) productObject.getDouble("fat_100g").toString() else "",
                            protein = if (productObject.has("proteins_100g")) productObject.getDouble("proteins_100g").toString() else "",
                            uri = if (productObject.has("image_url")) productObject.getString("image_url") else "",
                            weight = "100",
                            qty = "1",
                            favour = "",
                            key = if (productObject.has("code")) productObject.getString("code") else ""
                        )
                        dataList.add(foodItem)
                    }

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}