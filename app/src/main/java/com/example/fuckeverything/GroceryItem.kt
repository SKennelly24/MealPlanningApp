package com.example.fuckeverything

/**
* Class to hold the variables associated with the grocery item
 */
class GroceryItem(val name: String, val type: String, var start: Boolean, var checked: Boolean) {
    override fun toString(): String {
        return "$type $name $start"
    }
}