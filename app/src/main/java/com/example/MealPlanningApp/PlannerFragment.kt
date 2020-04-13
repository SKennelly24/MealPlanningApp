package com.example.MealPlanningApp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.io.*


/*
* Planner fragment which shows the weekdays and the planned meals for each day
* - Writes all the plan to a JSON file so there is data persistence
* - Can add meals to a particular day, these are also added into a new meals array in the activity
* - Can clear the plan for the week
* TO DO
* - Press on a meal to delete it
 */
class PlannerFragment : Fragment() {
    private lateinit var mealListArrays: Array<MealList>
    private var newMeals: ArrayList<String> = arrayListOf()
    private lateinit var activityMain: MainActivity
    private lateinit var mealArrays: Array<ArrayList<String>>
    private lateinit var dialogView : View
    private var index = -1

    /*
    * Reads the plan from the JSON file and gets the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mealArrays = arrayOf(arrayListOf(), arrayListOf(),
        arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(),arrayListOf())
        readPlanfromJson()
        activityMain = activity as MainActivity
    }

    /*
    * Connects the fragment to the fragment planner view,
    * sets up the add and clear buttons and their listeners,
    * connects the list views to the lists of meals
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogView = inflater.inflate(R.layout.add_meal_dialog, container, false)
        val view = inflater.inflate(R.layout.fragment_planner, container, false)
        val addButton: Button = view.findViewById(R.id.add_button)
        val clearButton : Button = view.findViewById(R.id.clear_button)


        addButton.setOnClickListener {
            val orientation = activityMain.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                createHorizontalMealDialog()
            } else {
                createVerticalMealDialog()
            }
        }
        clearButton.setOnClickListener{
            clearMeals()
        }

        makeMealList(view)
        showMeals()
        return view
    }

    /*
    * Writes the planner to the JSON file,
    * adds the new meals to the new meals variable
     */
    override fun onStop() {
        super.onStop()
        writePlantoJson()
        activityMain.newMeals = newMeals
    }

    /*
    * Creates the alert add meal dialog,
    * includes
    *   - Single choice items to choose day
    *   - Title
    *   - Edit text box to write meal
     */
    private fun createHorizontalMealDialog() {
        val taskEditText : EditText = dialogView.findViewById(R.id.editText)
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Add a Meal?")
        builder?.setView(dialogView)
        builder?.setPositiveButton("OK"
        ) { _, _ ->
            val index = findWeekdayClicked()
            val textString = taskEditText.text.toString()
            updateList(index, textString)
        }?.setNegativeButton("Cancel", null)?.create()
        builder?.show()
    }

    private fun createVerticalMealDialog() {
        val days = arrayOf("Mon", "Tues", "Wed", "Thu", "Fri", "Sat", "Sun")
        val taskEditText = EditText(context)
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Add an item?")?.setSingleChoiceItems(days, -1
        ) { _, i ->
            index = i
        }?.setPositiveButton("OK"
        ) { _, _ ->
            val textString = taskEditText.text.toString()
            updateList(index, textString)
        }?.setNegativeButton("Cancel", null)?.setView(taskEditText)?.create()?.show()
    }

    private fun findWeekdayClicked() : Int{
        var dayIndex = -1
        val days = arrayOf("M", "Tu", "W", "Th", "F", "Sa", "Su")
        val radioGroup : RadioGroup = dialogView.findViewById(R.id.radioButtons)
        val selectedButton : RadioButton = dialogView.findViewById(radioGroup.checkedRadioButtonId)
        for (i in days.indices) {
            if (days[i] == selectedButton.text) {
                dayIndex = i
            }
        }
        return dayIndex

    }

    /*
    * Clears all the meals and updates the list views
     */
    private fun clearMeals() {
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

    /*
    * Creates the MealList variables to hold the list view, text view and list for
    * each of the days of the week
     */
    private fun makeMealList(view: View) {
        val mondayListViewVar: ListView = view.findViewById(R.id.monday_listview)
        val tuesdayListViewVar: ListView = view.findViewById(R.id.tuesday_listview)
        val wednesdayListViewVar: ListView = view.findViewById(R.id.wednesday_listview)
        val thursdayListViewVar: ListView = view.findViewById(R.id.thursday_listview)
        val fridayListViewVar: ListView = view.findViewById(R.id.friday_listview)
        val saturdayListViewVar: ListView = view.findViewById(R.id.saturday_listview)
        val sundayListViewVar: ListView = view.findViewById(R.id.sunday_listview)

        mealListArrays = arrayOf(
            MealList(mealArrays[0], mondayListViewVar, view.findViewById(R.id.monday_text)),
            MealList(mealArrays[1], tuesdayListViewVar, view.findViewById(R.id.tuesday_text)),
            MealList(mealArrays[2], wednesdayListViewVar, view.findViewById(R.id.wednesday_text)),
            MealList(mealArrays[3], thursdayListViewVar, view.findViewById(R.id.thursday_text)),
            MealList(mealArrays[4], fridayListViewVar, view.findViewById(R.id.friday_text)),
            MealList(mealArrays[5], saturdayListViewVar, view.findViewById(R.id.saturday_text)),
            MealList(mealArrays[6], sundayListViewVar, view.findViewById(R.id.sunday_text))
        )
    }

    /*
    * Attaches all the mealList list views to their appropriate adapters
     */
    private fun showMeals() {
        for (i in mealListArrays.indices) {
            val adapter = context?.let {
                ArrayAdapter<String>(
                    it,
                    R.layout.activity_listview, mealListArrays[i].meals
                )
            }
            mealListArrays[i].listview_var.adapter = adapter
        }
    }
    private fun getDayIndex(day: String) : Int {
        var index = -1
        when(day) {
            "Monday" -> index = 0
            "Tuesday" -> index = 1
            "Wednesday" -> index = 2
            "Thursday" -> index = 3
            "Friday" -> index = 4
            "Saturday" -> index = 5
            "Sunday" -> index = 6
        }
        return index
    }

    /*
    * Reads the plan from the JSON file into the appropriate arrays
     */
    private fun readPlanfromJson() {
        try {
            //Opens file
            val file = context?.openFileInput("planner.json")
            val reader = JsonReader(InputStreamReader(file as InputStream))
            Log.d("Reading", "file")

            reader.beginObject()
            while (reader.hasNext()) {
                //Reading each day
                val name = reader.nextName()
                Log.d("Week day", name)
                index = getDayIndex(name)
                reader.beginArray()
                while(reader.hasNext()) {
                    //Reading the meals for the day
                    val meal = reader.nextString()
                    Log.d("item", meal)
                    mealArrays[index].add(meal)
                }
                reader.endArray()
            }
            reader.endObject()
            reader.close()
            Log.d("Closing", "File")

        } catch (e: FileNotFoundException) {
            Log.d("item", "no items")
        } catch (e: EOFException) {
            Log.d("nothing", "in file")
        }
    }
    /*
    * Updates the list with the new item and its adapter
     */
    private fun updateList(day_num :Int, meal_text: String) {
        Log.d("$day_num", meal_text)
        if (index != -1) {
            mealListArrays[day_num].meals.add(meal_text)
            newMeals.add(meal_text)
            //Rejoins the adapter to the appropriate list view
            val weekdayAdapter = context?.let {
                ArrayAdapter<String>(
                    it,
                    R.layout.activity_listview, mealListArrays[day_num].meals
                )
            }
            mealListArrays[day_num].listview_var.adapter = weekdayAdapter

            //Animates the appropriate text view
            val animation = AnimationUtils.loadAnimation(context, R.anim.sample_animation)
            mealListArrays[day_num].text_var.startAnimation(animation)
        }
    }
    /*
    * Finds the appropriate day string given the index
     */
    private fun findDayString(index : Int) : String {
        var day = ""
        when(index) {
            0 -> day = "Monday"
            1 -> day = "Tuesday"
            2 -> day = "Wednesday"
            3 -> day = "Thursday"
            4 -> day = "Friday"
            5 -> day = "Saturday"
            6 -> day = "Sunday"
        }
        return day
    }

    /*
    * Writes the plan to JSON
     */
    private fun writePlantoJson() {
        val file = context?.openFileOutput("planner.json", Context.MODE_PRIVATE)
        val writer = JsonWriter(OutputStreamWriter(file as OutputStream))
        writer.setIndent("  ")
        Log.d("Writer", "Starting Writing")
        writer.beginObject()
        Log.d("Writer", "Begun")
        for (i in mealListArrays.indices) {
            Log.d("index", "$i")
            writer.name(findDayString(i))
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
