package com.example.fuckeverything

class GroceryItem(val name: String, val type: String, var start: Boolean, var checked: Boolean) {
    override fun toString(): String {
        return "$type $name $start"
    }
}