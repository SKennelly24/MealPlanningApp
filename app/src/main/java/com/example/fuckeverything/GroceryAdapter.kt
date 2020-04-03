package com.example.homepagea

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fuckeverything.GroceryItem
import com.example.fuckeverything.GroceryViewHolder
import com.example.fuckeverything.MealViewHolder
import com.example.fuckeverything.R
import com.squareup.picasso.Picasso

class GroceryAdapter (val context: Context,
                   val grocery_list: List<GroceryItem>,
                   val clickListener: (GroceryItem) -> Unit): RecyclerView.Adapter<GroceryViewHolder>()
{
    override fun getItemCount(): Int = grocery_list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grocery_item, parent, false)
        val holder = GroceryViewHolder(view)

        view.setOnClickListener {
            clickListener(grocery_list[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, i: Int) {
        holder.text.text = grocery_list[i].name
        when(grocery_list[i].type) {
            "Produce" -> holder.image.setImageResource(R.drawable.trolley)
            "Canned" -> holder.image.setImageResource(R.drawable.trolley)
            "Meat" -> holder.image.setImageResource(R.drawable.trolley)
            "Dairy" -> holder.image.setImageResource(R.drawable.trolley)
            "Frozen" -> holder.image.setImageResource(R.drawable.trolley)
        }
    }

}