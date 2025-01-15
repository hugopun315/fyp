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
import com.example.fyp.FoodTesting
import com.example.fyp.R
import com.example.fyp.foodDetails

class FoodAdapter(private val context: Context, private val foodList: List<FoodTesting>, private val time: String, private val date: String, private val value: String) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

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
        Glide.with(context).load(item.image_url).into(holder.image)
        holder.title.text = item.product_name
        holder.cal.text = item.energy_kcal_100g.toString()
        holder.recCard.setOnClickListener {
            val intent = Intent(context, foodDetails::class.java).apply {
                putExtra("image", item.image_url)
                putExtra("title", item.product_name)
                putExtra("car", item.carbohydrates_100g.toString())
                putExtra("pro", item.proteins_100g.toString())
                putExtra("fat", item.fat_100g.toString())
                putExtra("cal", item.energy_kcal_100g.toString())
                putExtra("time", time) // Pass the time extra
                putExtra("date", date)
                putExtra("value", value)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = foodList.size
}
