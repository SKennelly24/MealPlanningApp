package com.example.fuckeverything

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
* Class to implement a view holder for the grocery items
 */
class GroceryViewHolder (view : View) : RecyclerView.ViewHolder(view){
    val text: TextView = view.findViewById(R.id.title)
    val image: ImageView = view.findViewById(R.id.type_image)
    val headerTitle: TextView = view.findViewById(R.id.header)
    val checkBox : CheckBox = view.findViewById(R.id.checkBox)
}