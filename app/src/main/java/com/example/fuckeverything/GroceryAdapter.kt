package com.example.homepagea

import android.content.Context
import android.util.Log
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
    var checkedItems : ArrayList<GroceryItem> = arrayListOf()

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
        Log.d("Bind", "${grocery_list[i].start}")
        holder.text.text = grocery_list[i].name
        when(grocery_list[i].type) {
            "Produce" -> holder.image.setImageResource(R.drawable.produce)
            "Canned" -> holder.image.setImageResource(R.drawable.can)
            "Meat" -> holder.image.setImageResource(R.drawable.meat)
            "Dairy" -> holder.image.setImageResource(R.drawable.dairy)
            "Frozen" -> holder.image.setImageResource(R.drawable.frozen)
        }
        if (grocery_list[i].start) {
            holder.headerTitle.text = grocery_list[i].type
            holder.headerTitle.textSize = 18F
        } else {
            holder.headerTitle.text = ""
            holder.headerTitle.textSize = 0F
        }
        holder.checkBox.setOnClickListener{
            Log.d("Checked", grocery_list[i].name)
            if (holder.checkBox.isChecked) {
                checkedItems.add(grocery_list[i])
                grocery_list[i].checked = true
            } else {
                checkedItems.remove(grocery_list[i])
                grocery_list[i].checked = false
            }
        }
        holder.checkBox.isChecked = grocery_list[i].checked
    }

}