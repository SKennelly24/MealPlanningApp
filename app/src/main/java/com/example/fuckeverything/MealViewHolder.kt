package com.example.fuckeverything

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MealViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val mealText: TextView= view.findViewById(R.id.title)
    val mealImage: ImageView= view.findViewById(R.id.meal_image)
}