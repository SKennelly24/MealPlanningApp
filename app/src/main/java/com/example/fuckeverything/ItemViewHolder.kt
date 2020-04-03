package com.example.fuckeverything

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val itemName: TextView = view.findViewById(R.id.title)
    val typeImage: ImageView = view.findViewById(R.id.type_image)
}