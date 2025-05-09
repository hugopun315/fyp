package com.example.fyp.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fyp.Food
import com.example.fyp.R
import com.example.fyp.foodDetails

class FoodAdapter(private val context: Context, private val foodList: List<Food>, private val time: String, private val date: String, private val value: String) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.recImage)
        val title: TextView = itemView.findViewById(R.id.title)
        val cal: TextView = itemView.findViewById(R.id.calories)
        val recCard: CardView = itemView.findViewById(R.id.recCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = foodList[position]
        Glide.with(context).load(item.uri).into(holder.image)
        holder.title.text = item.name
        holder.cal.text = "calories: " +item.calories
        holder.recCard.setOnClickListener {
            val intent = Intent(context, foodDetails::class.java).apply {
                putExtra("image", item.uri)
                putExtra("title", item.name)
                putExtra("car", item.carbohydrates.toString())
                putExtra("pro", item.protein.toString())
                putExtra("fat", item.fat.toString())
                putExtra("cal", item.calories.toString())
                putExtra("weight", item.weight.toString())
                putExtra("qty", item.qty)
                putExtra("time", time) // Pass the time extra
                putExtra("date", date)
                putExtra("key", item.key)
                putExtra("brands", item.brands)
                putExtra("value", value)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = foodList.size
}
