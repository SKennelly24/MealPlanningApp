package com.example.MealPlanningApp

/**
* Class to hold the variables associated with the grocery item
 */
class GroceryItem(val name: String, val type: String, var start: Boolean, var checked: Boolean, var amount: Int) {
    override fun toString(): String {
        return "$type $name $amount $start "
    }
}