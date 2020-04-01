package com.example.fuckeverything

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonToken
import android.util.JsonWriter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlannerFragment : Fragment() {
    private val days = arrayOf("Mon", "Tues", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var index: Int = -1
    private lateinit var mealListArrays: Array<MealList>
    private var all_meals: ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_planner, container, false)
        val addButton: Button = view.findViewById(R.id.add_button)
        val clearButton : Button = view.findViewById(R.id.clear_button)



        addButton.setOnClickListener {
            createMealDialog()
        }
        clearButton.setOnClickListener{
            clearMeals()
        }

        makeMealList(view)
        readPlanfromJson()
        showMeals()
        return view
    }

    override fun onPause() {
        super.onPause()
        writeMealstoJson()
        writePlantoJson()
    }

    fun createMealDialog() {
        val taskEditText = EditText(context)
        val builder = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add a Meal?")
                .setSingleChoiceItems(days, -1,
                    DialogInterface.OnClickListener{ _, i ->
                        index = i
                    })
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { _, _ ->
                        val textString = taskEditText.text.toString()
                        updateList(index, textString)

                    })
                .setNegativeButton("Cancel", null)
                .setView(taskEditText)
                .create()
                .show()
        }

    }

    fun clearMeals() {
        for (meal_lists in mealListArrays) {
            meal_lists.meals.clear()
            val adapter = context?.let {
                ArrayAdapter<String>(
                    it,
                    R.layout.activity_listview, meal_lists.meals
                )
            }
            meal_lists.listview_var.adapter = adapter
        }
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
                Log.d("item", meal)
                all_meals.add(meal)
            }
            reader.endArray()
            reader.endObject()
            reader.close()
        } catch (e: FileNotFoundException) {
            Log.d("item", "no items")
        }

    }

    fun makeMealList(view: View) {
        val mondayListViewVar: ListView = view.findViewById(R.id.monday_listview)
        val tuesdayListViewVar: ListView = view.findViewById(R.id.tuesday_listview)
        val wednesdayListViewVar: ListView = view.findViewById(R.id.wednesday_listview)
        val thursdayListViewVar: ListView = view.findViewById(R.id.thursday_listview)
        val fridayListViewVar: ListView = view.findViewById(R.id.friday_listview)
        val saturdayListViewVar: ListView = view.findViewById(R.id.saturday_listview)
        val sundayListViewVar: ListView = view.findViewById(R.id.sunday_listview)

        mealListArrays = arrayOf(MealList(arrayListOf(), mondayListViewVar),
            MealList(arrayListOf(), tuesdayListViewVar),
            MealList(arrayListOf(), wednesdayListViewVar),
            MealList(arrayListOf(), thursdayListViewVar),
            MealList(arrayListOf(), fridayListViewVar),
            MealList(arrayListOf(), saturdayListViewVar),
            MealList(arrayListOf(), sundayListViewVar))
    }

    fun showMeals() {
        for (meal_lists in mealListArrays) {
            val adapter = context?.let {
                ArrayAdapter<String>(
                    it,
                    R.layout.activity_listview, meal_lists.meals
                )
            }
            meal_lists.listview_var.adapter = adapter
        }
    }

    fun readPlanfromJson() {
        var index = 0
        try {
            val file = context?.openFileInput("planner.json")
            val reader = JsonReader(InputStreamReader(file))
            Log.d("reading", "file")
            val peeked1 = reader.peek()
            Log.d("peeked 1", "$peeked1")
            if (peeked1 == JsonToken.BEGIN_OBJECT) {
                reader.beginObject()
                while (reader.hasNext()) {
                    val name = reader.nextName()
                    Log.d("Week day", name)
                    when(name) {
                        "Monday" -> index = 0
                        "Tuesday" -> index = 1
                        "Wednesday" -> index = 2
                        "Thursday" -> index = 3
                        "Friday" -> index = 4
                        "Saturday" -> index = 5
                        "Sunday" -> index = 6
                    }
                    val peeked2 = reader.peek()
                    Log.d("peeked 2", "$peeked2")
                    if (peeked2 == JsonToken.BEGIN_ARRAY) {
                        reader.beginArray()
                        val peeked3 = reader.peek()
                        Log.d("peeked 3", "$peeked3")
                        if (peeked3 != JsonToken.END_ARRAY) {
                            while(reader.hasNext()) {
                                val meal = reader.nextString()
                                Log.d("item", meal)
                                mealListArrays[index].meals.add(meal)
                            }
                        }
                        reader.endArray()
                    }
                }
                reader.endObject()
            }
            reader.close()
            Log.d("Closing", "File")
        } catch (e: FileNotFoundException) {
            Log.d("item", "no items")

        } catch (e: EOFException) {
            Log.d("nothing", "in file")
        }

    }


    fun updateList(day_num :Int, meal_text: String) {
        Log.d("$day_num", meal_text)
        mealListArrays[day_num].meals.add(meal_text)
        all_meals.add(meal_text)
        val weekday_adapter = context?.let {
            ArrayAdapter<String>(
                it,
                R.layout.activity_listview, mealListArrays[day_num].meals
            )
        }
        mealListArrays[day_num].listview_var.adapter = weekday_adapter
    }

    fun writeMealstoJson() {
        val file = context?.openFileOutput("meals.json", Context.MODE_PRIVATE)
        val writer = JsonWriter(OutputStreamWriter(file))
        writer.setIndent("  ")
        Log.d("Writer", "Starting Writing")
        writer.beginObject()
        writer.name("Meals")
        writer.beginArray()
        for (meal in all_meals) {
            Log.d("meal", meal)
            writer.value(meal)
        }
        Log.d("Writer", "Finished Writing")
        writer.endArray()
        writer.endObject()
        writer.close()
    }

    fun writePlantoJson() {
        val file = context?.openFileOutput("planner.json", Context.MODE_PRIVATE)
        val writer = JsonWriter(OutputStreamWriter(file))
        writer.setIndent("  ")
        Log.d("Writer", "Starting Writing")
        writer.beginObject()
        Log.d("Writer", "Begun")
        for (i in 0..mealListArrays.size-1) {
            Log.d("index", "$i")
            when(i) {
                0 -> writer.name("Monday")
                1 -> writer.name("Tuesday")
                2 -> writer.name("Wednesday")
                3 -> writer.name("Thursday")
                4 -> writer.name("Friday")
                5 -> writer.name("Saturday")
                6 -> writer.name("Sunday")
            }
            writer.beginArray()
            for (meal in mealListArrays[i].meals) {
                writer.value(meal)
            }
            writer.endArray()
        }
        writer.endObject()
        Log.d("Writer", "Finished Writing")
        writer.close()
    }
}
