package com.example.fuckeverything

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroceryViewHolder (view : View) : RecyclerView.ViewHolder(view){
    val text: TextView = view.findViewById(R.id.title)
    val image: ImageView = view.findViewById(R.id.type_image)
}