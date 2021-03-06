package com.example.homepagea

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.MealPlanningApp.MealViewHolder
import com.example.MealPlanningApp.R
import com.squareup.picasso.Picasso
/**
 * Implements the adapter needed for the meal recycler view
 */
class MealAdapter (val context: Context,
                       val meal_list: List<Meal>,
                   val clickListener: (Meal) -> Unit): RecyclerView.Adapter<MealViewHolder>()
{
    override fun getItemCount(): Int = meal_list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.meal_item, parent, false)
        val holder = MealViewHolder(view)

        view.setOnClickListener {
            clickListener(meal_list[holder.adapterPosition])
        }

        return holder
    }
    /**
    * Updates the text and the image for the particular item
     */
    override fun onBindViewHolder(holder: MealViewHolder, i: Int) {
        holder.mealText.text = meal_list[i].text
        Picasso.get().load(meal_list[i].image_url).into(holder.mealImage)
    }

}