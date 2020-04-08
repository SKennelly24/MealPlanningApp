package com.example.fuckeverything

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepagea.Meal
import com.example.homepagea.MealAdapter
import com.example.homepagea.MealDownloader
import java.io.*
/**
 * Meal fragment which shows the previous meals and the suggested meals
 * - Shows all the previously used meals
 * - Shows suggested meals from the tasty api
 * TO DO
 * - Add ability to add a meal to a certain day and the groceries
 */
class MealsFragment : Fragment() {
    private lateinit var mealListview: ListView
    private var meals: ArrayList<String> = arrayListOf()
    private lateinit var mealPicker: RecyclerView
    private lateinit var activityMain: MainActivity
    var mealList: List<Meal> = listOf()
        set(value) {
            field = value
            mealPicker.adapter = context?.let { context ->
                MealAdapter(context, field) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                    startActivity(intent)
                }
            }

        }

    /**
     * Reads the JSON file for meals
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectUpdateMeals()
    }

    /**
     * Sets up the recyclerview, buttons and list view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meals, container, false)
        mealListview = view.findViewById(R.id.meals_listview)
        mealPicker = view.findViewById(R.id.mealPicker)
        val clearButton : Button = view.findViewById(R.id.clear_button)

        clearButton.setOnClickListener{
            clearMeals()
        }

        updateMeals()
        setUpSuggestedMeals()

        return view
    }

    /**
     * Writes the meals to the JSON file
     */
    override fun onStop() {
        super.onStop()
        writeMealstoJson(meals)
    }

    /**
     * Sets up the recyclerview and its asynctask for the suggested meals
     */
    private fun setUpSuggestedMeals() {
        MealDownloader(this).execute()

        val layoutManager = LinearLayoutManager(context)
        mealPicker.layoutManager = layoutManager

        val decoration = DividerItemDecoration(context, layoutManager.orientation)
        mealPicker.addItemDecoration(decoration)
    }

    /**
     * Collects the meals from the JSON file and the activities new meals variable,
     * puts these in the meal variable
     */
    private fun collectUpdateMeals() {
        meals = readJsonMeals()
        activityMain = activity as MainActivity
        val newMeals : ArrayList<String> = activityMain.newMeals
        for (meal in newMeals) {
            if (meal !in meals) {
                meals.add(meal)
            }
        }
        activityMain.newMeals.clear()
    }

    /**
     * Updates the adapter to the latest meals list
     */
    private fun updateMeals() {
        val adapter = context?.let {
            ArrayAdapter<String>(
                it,
                R.layout.activity_listview, meals
            )
        }
        mealListview.adapter = adapter
    }

    /**
     * Clears the meals and updates the list view
     */
    private fun clearMeals() {
        meals.clear()
        updateMeals()
        writeMealstoJson(meals)
        activityMain.newMeals.clear()
    }

    /**
     * Writes the meals to the JSON file
     */
    private fun writeMealstoJson(meals : ArrayList<String>) {
        val file = context?.openFileOutput("meals.json", Context.MODE_PRIVATE)
        val writer = JsonWriter(OutputStreamWriter(file as OutputStream))
        writer.setIndent("  ")
        Log.d("All meals", meals.toString())
        Log.d("Writer", "Starting Writing")
        writer.beginObject()
        writer.name("Meals")
        writer.beginArray()
        for (meal in meals) {
            Log.d("meal", meal)
            writer.value(meal)
        }
        Log.d("Writer", "Finished Writing")
        writer.endArray()
        writer.endObject()
        writer.close()
    }

    /**
     * Reads the meals from the JSON file
     */
    private fun readJsonMeals() : ArrayList<String>{
        val readMeals :ArrayList<String> = arrayListOf()
        try {
            val file = context?.openFileInput("meals.json")
            val reader = JsonReader(InputStreamReader(file as InputStream))
            Log.d("item", "reader open")
            reader.beginObject()
            Log.d("name", reader.nextName())
            reader.beginArray()
            Log.d("item", "array started")
            while (reader.hasNext()) {
                val meal = reader.nextString()
                Log.d("item", meal)
                readMeals.add(meal)
            }
            reader.endArray()
            reader.endObject()
            reader.close()
            Log.d("Meals", meals.toString())
        } catch (e: FileNotFoundException) {
            Log.d("item", "no items")
        }
        return readMeals
    }
}