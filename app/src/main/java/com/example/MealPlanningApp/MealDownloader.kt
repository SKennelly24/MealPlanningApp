package com.example.MealPlanningApp

import android.os.AsyncTask
import android.util.Log
import java.lang.ref.WeakReference
import java.net.URL
import com.example.homepagea.Meal
import org.json.JSONObject
import com.mashape.unirest.http.Unirest
import org.json.JSONException

const val URL = "https://tasty.p.rapidapi.com/recipes/list?tags=dinner&from=0&sizes=100"
const val HOST = "tasty.p.rapidapi.com"
const val KEY = "8db5bf4bc1msh54ce001970e013dp1be510jsn1b009a6048a3"

/**
 * Implements the AsyncTask to download the meals from the tasty portion of the rapid api
 */
class MealDownloader(private val activity: MealsFragment) : AsyncTask<URL, Void, List<Meal>>() {
    private val context = WeakReference(activity)

    override fun doInBackground(vararg params: URL?): List<Meal>? {
        val mealList : List<Meal>
        mealList = try {
            val result = getJson()
            //Collects the JSON array and makes a meal object with the relevant fields
            val headlinesJson = result.getJSONArray("results")
            (0 until headlinesJson.length()).map { i ->
                val headline = headlinesJson.getJSONObject(i)
                Meal(headline.getString("name"), headline.getString("video_url"),
                    headline.getString("thumbnail_url"))
            }
        } catch (e: JSONException) {
            Log.d("Error", "AsyncTask")
            listOf()
        }
        return mealList
    }

    override fun onPostExecute(meal_list: List<Meal>) {
        super.onPostExecute(meal_list)
        context.get()?.mealList = meal_list
    }

    /**
    * Collects the JSON object from the rapid api tasty section
     */
    private fun getJson(): JSONObject {
        val response =
            Unirest.get(URL)
                .header("x-rapidapi-host", HOST)
                .header("x-rapidapi-key", KEY)
                .asString()
        return JSONObject(response.body)
    }

}