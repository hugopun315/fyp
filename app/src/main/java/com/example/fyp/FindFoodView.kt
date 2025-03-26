package com.example.fyp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FoodAdapter
import com.example.fyp.databinding.ActivityFindFoodViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.vision.barcode.common.Barcode
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FindFoodView : AppCompatActivity() {
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var eventListener: ValueEventListener
    private lateinit var homeButton1: ImageView
    private lateinit var homeButton2: TextView
    private lateinit var aiButton1: ImageView
    private lateinit var aiButton2: TextView
    private lateinit var profileButtonP1: ImageView
    private lateinit var profileButtonP2: TextView
    private lateinit var searchButton: Button
    private lateinit var searchBar: EditText
    private lateinit var addOwnFood: Button
    private lateinit var scannbutton: Button
    private val camerPermission = android.Manifest.permission.CAMERA
    private lateinit var binding: ActivityFindFoodViewBinding
    private var productID: String = ""
    private var barcodeFood: Food? = null
    private var context: Context = this

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // start scanner
                startScanner()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindFoodViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val todayDate = getCurrentDate()
        val bundle = intent.extras
        val time = bundle?.getString("time") ?: ""
        Log.d("FindFoodView", "Binding initialized")

        binding.scanBarcode.setOnClickListener {
            requestCameraAndStartScanner()
        }

        foodRecyclerView = findViewById(R.id.foodList)
        homeButton1 = findViewById(R.id.imageViewHome)
        homeButton2 = findViewById(R.id.textViewHome)
        profileButtonP1 = findViewById(R.id.imageViewProfile)
        profileButtonP2 = findViewById(R.id.textViewProfile)
        aiButton1 = findViewById(R.id.imageViewAI)
        aiButton2 = findViewById(R.id.textViewAI)
        searchButton = findViewById(R.id.search_button)
        searchBar = findViewById(R.id.search_bar)
        addOwnFood = findViewById(R.id.addOwnFood)
        scannbutton = findViewById(R.id.scanBarcode)



        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = ArrayList<Food>()
        val adapter = FoodAdapter(this, dataList, time, todayDate, "A")
        foodRecyclerView.adapter = adapter

        // Load data from Firebase initially
        loadFirebaseData(adapter, dataList)

        searchButton.setOnClickListener {


            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchFood(query, adapter, dataList)
            }


        }


        // bottom bar
        homeButton1.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }
        homeButton2.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }

        aiButton1.setOnClickListener {
            val intent = Intent(this, chatGPTAPI::class.java)
            startActivity(intent)
        }
        aiButton2.setOnClickListener {
            val intent = Intent(this, chatGPTAPI::class.java)
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



        addOwnFood.setOnClickListener {
            val intent = Intent(this, uploadFood::class.java)
            startActivity(intent)
        }
    }

    private fun requestCameraAndStartScanner() {
        if (isPermissionGranted(camerPermission)) {
            Log.d("FindFoodView", "Camera permission granted")
            startScanner()
        } else {
            Log.d("FindFoodView", "Camera permission not granted")
            requestCameraPermission()
        }
    }

    private fun startScanner() {
        ScannerActivity.startScanner(this) { barcodes ->
            barcodes.forEach { barcode ->
                Log.d("FindFoodView", "Barcode Value Type: ${barcode.valueType}")
                when (barcode.valueType) {
                    Barcode.TYPE_CONTACT_INFO -> {
                        barcode.contactInfo?.let {

                            Log.d("FindFoodView", "Contact Info: $productID")
                            runOnUiThread {
                                if (!isFinishing) {
                                    Log.d("FindFoodView", "Showing Toast on main thread")
                                    Toast.makeText(this, productID, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } ?: Log.d("FindFoodView", "Contact Info is null")
                    }

                    Barcode.TYPE_TEXT -> {
                        productID = barcode.displayValue ?: ""
                        Log.d("FindFoodView", "Text: $productID")
                        runOnUiThread {
                            if (!isFinishing) {
                                Log.d("FindFoodView", "Showing Toast on main thread")
                                Toast.makeText(this, productID, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    else -> {
                        productID = barcode.displayValue ?: ""
                        Log.d("FindFoodView", "Other barcode type scanned with value: $productID")
                        runOnUiThread {
                            if (!isFinishing) {
                                Log.d("FindFoodView", "Showing Toast on main thread")
                                Toast.makeText(this, productID, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }


                if (productID!="") {
                    val bundle = intent.extras
                    searchByProductID(bundle?.getString("time") ?: "", getCurrentDate(), "A")
                }
            }
        }
    }

    private fun requestCameraPermission() {
        when {
            shouldShowRequestPermissionRationale(camerPermission) -> {
                cameraPermissionRequest {
                    openPermissionSetting()
                }
            }

            else -> {
                requestPermissionLauncher.launch(camerPermission)
            }
        }
    }

    private fun loadFirebaseData(adapter: FoodAdapter, dataList: ArrayList<Food>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Demo Food")
        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Food::class.java)
                    dataClass?.let {
                        dataList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message) // Handle possible errors
            }
        }
        databaseReference.addValueEventListener(eventListener)
    }


    private fun  searchByProductID(time: String, date: String, value: String){
        val loadingProgressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE // Show loading indicator
        if (productID.isNotEmpty()) {

            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://world.openfoodfacts.net/api/v2/product/$productID&fields=product_name,carbohydrates_100g,energy-kcal_100g,fat_100g,proteins_100g,image_url")
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
                        val productObject = jsonObject.getJSONObject("product")

                        val foodItem = Food(
                            name = if (productObject.has("product_name")) productObject.getString(
                                "product_name"
                            ) else "API miss name",
                            carbohydrates = if (productObject.has("carbohydrates_100g")) productObject.getDouble(
                                "carbohydrates_100g"
                            ).toString() else "API miss carbohydrates data",
                            calories = if (productObject.has("energy-kcal_100g")) productObject.getDouble(
                                "energy-kcal_100g"
                            ).toString() else "API miss kcals data",
                            fat = if (productObject.has("fat_100g")) productObject.getDouble(
                                "fat_100g"
                            ).toString() else "API miss fats data",
                            protein = if (productObject.has("proteins_100g")) productObject.getDouble(
                                "proteins_100g"
                            ).toString() else "API miss proteins data",
                            uri = if (productObject.has("image_url")) productObject.getString(
                                "image_url"
                            ) else "",
                            weight = "100",
                            qty = "1",
                            favour = "F",
                            key = if (jsonObject.has("code")) jsonObject.getString("code") else "API missing data",
                            brands = if (productObject.has("brands")) productObject.getString(
                                "brands"
                            ) else "API missing data"
                        )
                        barcodeFood = foodItem

                        // Start the foodDetails activity with the product details
                        val intent = Intent(context, foodDetails::class.java).apply {
                            putExtra("image", barcodeFood?.uri)
                            putExtra("title", barcodeFood?.name)
                            putExtra("car", barcodeFood?.carbohydrates.toString())
                            putExtra("pro", barcodeFood?.protein.toString())
                            putExtra("fat", barcodeFood?.fat.toString())
                            putExtra("cal", barcodeFood?.calories.toString())
                            putExtra("weight", barcodeFood?.weight.toString())
                            putExtra("time", time) // Pass the time extra
                            putExtra("date", date)
                            putExtra("key", barcodeFood?.key)
                            putExtra("brands", barcodeFood?.brands)
                            putExtra("value", value)
                        }
                        context.startActivity(intent)

                    }

                    loadingProgressBar.visibility = View.INVISIBLE // Hide loading indicator
                }
            })
        }

    }
    private fun searchFood(query: String, adapter: FoodAdapter, dataList: ArrayList<Food>) {
        val loadingProgressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE // Show loading indicator
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://world.openfoodfacts.org/api/v2/search?categories_tags=$query&fields=product_name,carbohydrates_100g,energy-kcal_100g,fat_100g,proteins_100g,image_url,code,brands")
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
                            name = if (productObject.has("product_name")) productObject.getString("product_name") else "API miss name",
                            carbohydrates = if (productObject.has("carbohydrates_100g")) productObject.getDouble(
                                "carbohydrates_100g"
                            ).toString() else "API miss carbohydrates data",
                            calories = if (productObject.has("energy-kcal_100g")) productObject.getDouble(
                                "energy-kcal_100g"
                            ).toString() else "API miss kcals data",
                            fat = if (productObject.has("fat_100g")) productObject.getDouble("fat_100g")
                                .toString() else "API miss fats data",
                            protein = if (productObject.has("proteins_100g")) productObject.getDouble(
                                "proteins_100g"
                            ).toString() else "API miss proteins data",
                            uri = if (productObject.has("image_url")) productObject.getString("image_url") else "",
                            weight = "100",
                            qty = "1",
                            favour = "F",
                            key = if (productObject.has("code")) productObject.getString("code") else "API missing data",
                            brands = if (productObject.has("brands")) productObject.getString("brands") else "API missing data"
                        )
                        dataList.add(foodItem)
                    }
                    loadingProgressBar.visibility = View.INVISIBLE // Hide loading indicator
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