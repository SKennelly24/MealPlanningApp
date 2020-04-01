package com.example.fuckeverything

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepagea.Meal
import com.example.homepagea.MealAdapter
import com.example.homepagea.MealDownloader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL

const val KEY = "8db5bf4bc1msh54ce001970e013dp1be510jsn1b009a6048a3"

class MealsFragment : Fragment() {
    private lateinit var meal_listview: ListView
    private var meals: ArrayList<String> = arrayListOf()
    private lateinit var mealPicker: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        readJsonMeals()
        val view = inflater.inflate(R.layout.fragment_meals, container, false)
        meal_listview = view.findViewById(R.id.meals_listview)
        val adapter = context?.let {
            ArrayAdapter<String>(
                it,
                R.layout.activity_listview, meals
            )
        }
        meal_listview.adapter = adapter

        val parameters = mapOf("apiKey" to KEY)
        val url = parameterizeUrl("https://tasty.p.rapidapi.com/recipes/list", parameters)
        MealDownloader(this).execute(url)

        mealPicker = view.findViewById(R.id.mealPicker)
        val layoutManager = LinearLayoutManager(context)
        mealPicker.layoutManager = layoutManager

        val decoration = DividerItemDecoration(context, layoutManager.orientation)
        mealPicker.addItemDecoration(decoration)
        return view
    }

    fun readJsonMeals() {
        try {
            val file = context?.openFileInput("meals.json")
            val reader = JsonReader(InputStreamReader(file))
            Log.d("item", "reader open")
            reader.beginObject()
            Log.d("name", "${reader.nextName()}")
            reader.beginArray()
            Log.d("item", "array started")
            while (reader.hasNext()) {
                val meal = reader.nextString()
                Log.d("item", "$meal")
                meals.add(meal)
            }
            reader.endArray()
            reader.endObject()
            reader.close()
        } catch (e: FileNotFoundException) {
            Log.d("item", "no items")
        }

    }
    var meal_list: List<Meal> = listOf()
        set(value) {
            field = value
            mealPicker.adapter = context?.let {
                MealAdapter(it, field) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                    startActivity(intent)
                }
            }

        }
    fun parameterizeUrl(url: String, parameters: Map<String, String>): URL {
        val builder = Uri.parse(url).buildUpon()
        parameters.forEach { (key, value) -> builder.appendQueryParameter(key, value) }
        val uri = builder.build()
        return URL(uri.toString())
    }
}