package com.example.homepagea

import android.os.AsyncTask
import android.widget.Toast
import com.squareup.picasso.Picasso
import java.lang.ref.WeakReference
import java.net.URL
import android.widget.ImageView
import com.example.fuckeverything.MealsFragment
import org.json.JSONObject
import com.mashape.unirest.http.Unirest

class MealDownloader(val activity: MealsFragment) : AsyncTask<URL, Void, List<Meal>>() {
    private val context = WeakReference(activity)
    override fun doInBackground(vararg urls: URL): List<Meal> {
        val result = getJson(urls[0])

        val headlinesJson = result.getJSONArray("results")
        val meal_list = (0 until headlinesJson.length()).map { i ->
            val headline = headlinesJson.getJSONObject(i)
            Meal(headline.getString("name"), headline.getString("video_url"),
                headline.getString("thumbnail_url"))
        }
        return meal_list
    }

    override fun onPostExecute(meal_list: List<Meal>) {
        super.onPostExecute(meal_list)
        context.get()?.meal_list = meal_list
    }

    fun getJson(url: URL): JSONObject {

        val response =
            Unirest.get("https://tasty.p.rapidapi.com/recipes/list?tags=dinner&from=0&sizes=19")
                .header("x-rapidapi-host", "tasty.p.rapidapi.com")
                .header("x-rapidapi-key", "8db5bf4bc1msh54ce001970e013dp1be510jsn1b009a6048a3")
                .asString()

        return JSONObject(response.body)
    }

}