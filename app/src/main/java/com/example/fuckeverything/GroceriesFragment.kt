package com.example.fuckeverything

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.example.homepagea.Meal
import com.example.homepagea.MealAdapter
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

const val PRODUCE_INDEX = 0
const val CANNED_INDEX = 1
const val MEAT_INDEX = 2
const val DAIRY_INDEX = 3
const val FROZEN_INDEX = 4

class GroceriesFragment : Fragment() {
    private val item_types = arrayOf("Produce", "Canned", "Meat", "Dairy", "Frozen")
    private var index  = -1
    private var item_list = arrayListOf<String>()
    private var grocery_item_list = arrayListOf<GroceryItem>()
    private lateinit var adapter : GroceryAdapter
    private lateinit var groceryPicker: RecyclerView

    private var grocery_list: ArrayList<GroceryItem> = arrayListOf()
    private var ends = arrayOf(-1,-1,-1,-1,-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readGroceriesfromJson()
    }
    override fun onStop() {
        super.onStop()
        writeGroceriestoJson()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groceries, container, false)
        val addButton: Button = view.findViewById(R.id.add_button)
        val clearButton : Button = view.findViewById(R.id.clear_button)
        val buyButton : Button = view.findViewById(R.id.buy_button)

        addButton.setOnClickListener {
            createItemAddDialog()
        }

        buyButton.setOnClickListener {
            createBuyItemsDialog()
        }

        clearButton.setOnClickListener {
            val initial_size = grocery_list.size
            grocery_list.clear()
            ends = arrayOf(-1,-1,-1,-1,-1)
            groceryPicker.adapter?.notifyDataSetChanged()
        }

        groceryPicker = view.findViewById(R.id.items_recycler)
        val layoutManager = LinearLayoutManager(context)
        groceryPicker.layoutManager = layoutManager

        val decoration = DividerItemDecoration(context, layoutManager.orientation)
        groceryPicker.addItemDecoration(decoration)
        adapter = context?.let {
            GroceryAdapter(it, grocery_list) {
                createDeleteDialog(it)
            }
        }!!
        groceryPicker.adapter = adapter

        return view
    }

    fun buyItems() {
        val checked_items = adapter?.checkedItems
        Log.d("Checked Items", checked_items.toString())
        if (checked_items != null) {
            for (item in checked_items) {
                grocery_list.remove(item)
            }
            ends = arrayOf(-1,-1,-1,-1,-1)
            var product_index = 0
            for (x in grocery_list.indices) {
                grocery_list[x].checked = false
                when(grocery_list[x].type) {
                    "Produce" -> product_index = PRODUCE_INDEX
                    "Canned" -> product_index = CANNED_INDEX
                    "Meat" -> product_index = MEAT_INDEX
                    "Dairy" -> product_index = DAIRY_INDEX
                    "Frozen" -> product_index = FROZEN_INDEX
                }
                if (ends[product_index] == -1) {
                    grocery_list[x].start = true
                    ends[product_index] = x + 1
                } else {
                    grocery_list[x].start = false
                    ends[product_index] += 1
                }
            }
            adapter.checkedItems.clear()
            groceryPicker.adapter?.notifyDataSetChanged()
        }
    }

    fun createBuyItemsDialog() {
        Log.d("Buying", "items")
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Buy Items?")?.setPositiveButton("OK",
            DialogInterface.OnClickListener { _, _ ->
                buyItems()
            })?.setNegativeButton("Cancel", null)?.create()?.show()
    }

    fun removeGroceryItem(item: GroceryItem) {
        Log.d("Delete", "${item.name}")
        grocery_list.remove(item)
        Log.d("Removed", "${item.name}")

        var starts = arrayOf(false, false, false, false, false)
        ends = arrayOf(-1,-1,-1,-1,-1)
        for (x in grocery_list.indices) {
            var product_index = 0
            when(grocery_list[x].type) {
                "Produce" -> product_index = PRODUCE_INDEX
                "Canned" -> product_index = CANNED_INDEX
                "Meat" -> product_index = MEAT_INDEX
                "Dairy" -> product_index = DAIRY_INDEX
                "Frozen" -> product_index = FROZEN_INDEX
            }
            if (starts[product_index]){
                grocery_list[x].start = false
            } else {
                starts[product_index] = true
                grocery_list[x].start = true
            }
            ends[product_index] = x + 1
        }
        for (x in ends.indices) {
            Log.d("$x", ends[x].toString())
        }
        groceryPicker.adapter?.notifyDataSetChanged()
        Log.d("all items", grocery_list.toString())
    }


    fun createDeleteDialog(item: GroceryItem) {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Delete Item?")?.setPositiveButton("OK",
            DialogInterface.OnClickListener { _, _ ->
                removeGroceryItem(item)
            })?.setNegativeButton("Cancel", null)?.create()?.show()

    }

    fun createItemAddDialog() {
        val taskEditText = EditText(context)
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Add an item?")?.setSingleChoiceItems(item_types, -1,
            DialogInterface.OnClickListener { _, i ->
                index = i
            })?.setPositiveButton("OK",
            DialogInterface.OnClickListener { _, _ ->
                val textString = taskEditText.text.toString()
                addGroceryItem(textString, index)
            })?.setNegativeButton("Cancel", null)?.setView(taskEditText)?.create()?.show()
    }


    fun addGroceryItem(textString : String, item_index: Int) {
        var currentIndex = grocery_list.size
        var start = false
        for (x in ends.indices) {
            if (x == item_index) {
                if (ends[x] == -1) {
                    ends[x] = grocery_list.size + 1
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
        grocery_list.add(currentIndex, GroceryItem(textString, item_types[index], start, false))
        groceryPicker.adapter?.notifyItemRangeChanged(currentIndex, grocery_list.size - currentIndex)
        Log.d("all items", grocery_list.toString())
    }

    fun writeGroceriestoJson() {
        val file = context?.openFileOutput("groceries.json", Context.MODE_PRIVATE)
        val writer = JsonWriter(OutputStreamWriter(file))
        writer.setIndent("  ")
        Log.d("Writer", "Starting Writing")

        writer.beginObject()
        writer.name("Groceries")
        writer.beginArray()
        for (item in grocery_list) {
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

    fun readGroceriesfromJson() {
        try {
            val file = context?.openFileInput("groceries.json")
            val reader = JsonReader(InputStreamReader(file))
            Log.d("item", "reader open")
            reader.beginObject()
            Log.d("name", "${reader.nextName()}")
            var count = 0
            reader.beginArray()
            Log.d("item", "array started")
            while (reader.hasNext()) {
                count += 1
                reader.beginArray()
                val grocery = reader.nextString()
                val type = reader.nextString()
                val start = reader.nextBoolean()
                grocery_list.add(GroceryItem(grocery, type, start, false))
                when(type) {
                    "Produce" -> ends[PRODUCE_INDEX] = count
                    "Canned" -> ends[CANNED_INDEX] = count
                    "Meat" -> ends[MEAT_INDEX] = count
                    "Dairy" -> ends[DAIRY_INDEX] = count
                    "Frozen" -> ends[FROZEN_INDEX] = count
                }
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