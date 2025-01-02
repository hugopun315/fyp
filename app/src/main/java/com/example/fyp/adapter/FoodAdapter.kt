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

class FoodAdapter(private val context: Context, private val foodList: List<Food>, private val time: String) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

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
        holder.cal.text = item.calories
        holder.recCard.setOnClickListener {
            val intent = Intent(context, foodDetails::class.java).apply {
                putExtra("image", item.uri)
                putExtra("title", item.name + " " + item.weight + "g")
                putExtra("key", item.key)
                putExtra("weight", item.weight)
                putExtra("car", item.carbohydrates)
                putExtra("pro", item.protein)
                putExtra("fat", item.fat)
                putExtra("cal", item.calories)
                putExtra("fav", item.favour)
                putExtra("qty", item.qty)
                putExtra("time", time) // Pass the time extra
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = foodList.size
}



