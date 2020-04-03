package com.example.fuckeverything

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

class GroceriesFragment : Fragment() {
    private val item_types = arrayOf("Produce", "Canned", "Meat", "Dairy", "Frozen")
    private var index  = -1
    private var item_list = arrayListOf<String>()
    private var grocery_item_list = arrayListOf<GroceryItem>()
    private lateinit var groceryPicker: RecyclerView

    var grocery_list: ArrayList<GroceryItem> = arrayListOf()
        /*set(value) {
            field = value
            groceryPicker.adapter = context?.let {
                GroceryAdapter(it, field) {
                    Log.d("Delete", "item")
                }
            }

        }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groceries, container, false)
        val addButton: Button = view.findViewById(R.id.add_button)

        addButton.setOnClickListener {
            createItemAddDialog()
        }

        groceryPicker = view.findViewById(R.id.items_recycler)
        val layoutManager = LinearLayoutManager(context)
        groceryPicker.layoutManager = layoutManager

        val decoration = DividerItemDecoration(context, layoutManager.orientation)
        groceryPicker.addItemDecoration(decoration)
        groceryPicker.adapter = context?.let { GroceryAdapter(it, grocery_list) {
            Log.d("Delete", "item")
        } }

        return view
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
                item_list.add(textString)
                grocery_item_list.add(GroceryItem(textString, item_types[index]))
                grocery_list.add(GroceryItem(textString, item_types[index]))
                groceryPicker.adapter?.notifyItemChanged(-1)
                Log.d("item", textString)
            })?.setNegativeButton("Cancel", null)?.setView(taskEditText)?.create()?.show()
    }
}