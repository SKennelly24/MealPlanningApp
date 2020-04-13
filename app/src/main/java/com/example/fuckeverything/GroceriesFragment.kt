package com.example.fuckeverything

import android.content.Context
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepagea.GroceryAdapter
import java.io.*

//Indexes for the varities of items
const val PRODUCE_INDEX = 0
const val CANNED_INDEX = 1
const val MEAT_INDEX = 2
const val DAIRY_INDEX = 3
const val FROZEN_INDEX = 4
const val OTHER_INDEX = 5
/**
* Groceries fragment which shows the grocery list
* - Writes all the items to a JSON file so includes data persistence
* - Implements a recycler view
* - Can add items and they move into the correct place depending on type
* - Can "buy" items and the list will be resorted
* TO DO
* - Add ability to if there is multiple of the same thing to have the amount next to it
* - Add ability to add items to particular meals
 * - Add other catergories
 * - Get rid of delete function
 */
class GroceriesFragment : Fragment() {
    private val itemTypes = arrayOf("Produce", "Canned", "Meat", "Dairy", "Frozen", "Other")
    private var index  = -1
    private lateinit var adapter : GroceryAdapter
    private lateinit var groceryPicker: RecyclerView
    private var groceryList: ArrayList<GroceryItem> = arrayListOf()
    private var ends = arrayOf(-1,-1,-1,-1,-1, -1)

    /**
    * Reads the grocery items from the JSON file
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readGroceriesfromJson()
    }

    /**
    * Write the grocery items to the JSON file
     */
    override fun onPause() {
        super.onPause()
        writeGroceriestoJson()
    }

    /**
    * Connects the fragment groceries layout and sets up the buttons and recycler view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groceries, container, false)
        val addButton: Button = view.findViewById(R.id.add_button)
        //val clearButton : Button = view.findViewById(R.id.clear_button)
        val buyButton : Button = view.findViewById(R.id.buy_button)
        groceryPicker = view.findViewById(R.id.items_recycler)

        addButton.setOnClickListener {
            createItemAddDialog()
        }

        buyButton.setOnClickListener {
            createBuyItemsDialog()
        }

        /*clearButton.setOnClickListener {
            groceryList.clear()
            ends = arrayOf(-1,-1,-1,-1,-1,-1)
            groceryPicker.adapter?.notifyDataSetChanged()
        }*/

        initRecyclerView()
        return view
    }

    /**
    * Initialises the recycler view by connecting its adapter
     */
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        groceryPicker.layoutManager = layoutManager

        //Adding a divider decoration
        val decoration = DividerItemDecoration(context, layoutManager.orientation)
        groceryPicker.addItemDecoration(decoration)

        //Connect adapter to the recycler view
        adapter = context?.let { context ->
            GroceryAdapter(context, groceryList) {
                //createDeleteDialog(it)
            }
        }!!
        groceryPicker.adapter = adapter
    }

    /**
    * Finds the product index given the string name
     */
    private fun findProductIndex(itemType : String) : Int {
        var productIndex = -1
        when(itemType) {
            "Produce" -> productIndex = PRODUCE_INDEX
            "Canned" -> productIndex = CANNED_INDEX
            "Meat" -> productIndex = MEAT_INDEX
            "Dairy" -> productIndex = DAIRY_INDEX
            "Frozen" -> productIndex = FROZEN_INDEX
            "Other" -> productIndex = OTHER_INDEX
        }
        return productIndex
    }

    /**
    * "Buys" the items checked by taking them out of the list
    * and updating the list
     */
    private fun buyItems() {
        val checkedItems = adapter.checkedItems
        Log.d("Checked Items", checkedItems.toString())

        //Removes items from the grocery list
        for (item in checkedItems) {
            groceryList.remove(item)
        }

        //Resets the ends array, goes through new list and updates
        //if this the first of the item type and where next item should go in the ends array
        ends = arrayOf(-1,-1,-1,-1,-1, -1)
        for (x in groceryList.indices) {
            groceryList[x].checked = false
            val productIndex = findProductIndex(groceryList[x].type)

            if (ends[productIndex] == -1) {
                groceryList[x].start = true
                ends[productIndex] = x + 1
            } else {
                groceryList[x].start = false
                ends[productIndex] += 1
            }
        }
        //Clears the checked items variable and notifies of the data set change
        adapter.checkedItems.clear()
        groceryPicker.adapter?.notifyDataSetChanged()
    }

    /**
    * Creates the buy items dialog
     */
    private fun createBuyItemsDialog() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Buy Items?")?.setPositiveButton("OK"
        ) { _, _ ->
            buyItems()
        }?.setNegativeButton("Cancel", null)?.create()?.show()
    }
    /**
    * Removes the grocery item from the list and updates it
     */
    private fun removeGroceryItem(item: GroceryItem) {
        Log.d("Delete", item.name)
        groceryList.remove(item)

        //Checks if the item type is the same as the item removed and updates whether it is the
        // first or not and the ends array
        val starts = arrayOf(false, false, false, false, false)
        ends = arrayOf(-1,-1,-1,-1,-1,-1)
        for (x in groceryList.indices) {
            val productIndex = findProductIndex(groceryList[x].type)
            if (starts[productIndex]){
                groceryList[x].start = false
            } else {
                starts[productIndex] = true
                groceryList[x].start = true
            }
            ends[productIndex] = x + 1
        }
        groceryPicker.adapter?.notifyDataSetChanged()
    }

    /**
    * Creates a dialog to delete an item
     */
    private fun createDeleteDialog(item: GroceryItem) {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Delete Item?")?.setPositiveButton("OK"
        ) { _, _ ->
            removeGroceryItem(item)
        }?.setNegativeButton("Cancel", null)?.create()?.show()

    }
    /**
    * Creates a dialog to add an item to the list
     */
    private fun createItemAddDialog() {
        val taskEditText = EditText(context)
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Add an item?")?.setSingleChoiceItems(itemTypes, -1
        ) { _, i ->
            index = i
        }?.setPositiveButton("OK"
        ) { _, _ ->
            val textString = taskEditText.text.toString()
            addGroceryItem(textString, index)
        }?.setNegativeButton("Cancel", null)?.setView(taskEditText)?.create()?.show()
    }


    private fun addGroceryItem(textString : String, item_index: Int) {
        var currentIndex = groceryList.size
        var start = false
        for (x in ends.indices) {
            if (x == item_index) {
                if (ends[x] == -1) {
                    ends[x] = groceryList.size + 1
                    start = true
                } else {
                    currentIndex = ends[x]
                    ends[x] = ends[x] + 1
                }
            }
            else if (ends[x] > item_index) {
                ends[x] += 1
            }
            Log.d("$x", ends[x].toString())
        }
        Log.d("Index", currentIndex.toString())
        Log.d("Item", textString)
        Log.d("Start", start.toString())
        Log.d("Type", item_index.toString())
        groceryList.add(currentIndex, GroceryItem(textString, itemTypes[index], start, false))
        groceryPicker.adapter?.notifyItemRangeChanged(currentIndex, groceryList.size - currentIndex)
        Log.d("all items", groceryList.toString())
    }

    /**
    * Writes the grocery items to JSON
     */
    private fun writeGroceriestoJson() {
        val file = context?.openFileOutput("groceries.json", Context.MODE_PRIVATE)
        val writer = JsonWriter(OutputStreamWriter(file as OutputStream))
        writer.setIndent("  ")
        Log.d("Writer", "Starting Writing")

        writer.beginObject()
        writer.name("Groceries")
        writer.beginArray()
        for (item in groceryList) {
            writer.beginArray()
            Log.d("meal", item.name)
            writer.value(item.name)
            Log.d("type", item.type)
            writer.value(item.type)
            Log.d("start", item.start.toString())
            writer.value(item.start)
            writer.endArray()
        }
        Log.d("Writer", "Finished Writing")
        writer.endArray()
        writer.endObject()
        writer.close()
    }

    /**
    * Reads the groceries from the JSON file and adds them into the grocery list appropriately
     */
    private fun readGroceriesfromJson() {
        try {
            val file = context?.openFileInput("groceries.json")
            val reader = JsonReader(InputStreamReader(file as InputStream))
            Log.d("item", "reader open")
            reader.beginObject()
            Log.d("name", reader.nextName())
            var count = 0
            reader.beginArray()
            Log.d("item", "array started")
            while (reader.hasNext()) {
                count += 1
                reader.beginArray()
                val grocery = reader.nextString()
                val type = reader.nextString()
                val start = reader.nextBoolean()
                groceryList.add(GroceryItem(grocery, type, start, false))
                ends[findProductIndex(type)] = count
                Log.d("grocery", grocery)
                Log.d("type", type)
                Log.d("start", start.toString())

                reader.endArray()
            }
            reader.endArray()
            reader.endObject()

            reader.close()
        } catch (e: FileNotFoundException) {
            Log.d("item", "no items")
        }
    }

}