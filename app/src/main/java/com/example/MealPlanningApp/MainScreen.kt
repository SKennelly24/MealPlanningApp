package com.example.MealPlanningApp

import androidx.fragment.app.Fragment

/**
 * Screens available for display in the main screen, with their respective titles,
 * icons, and menu item IDs and fragments.
 */
enum class MainScreen(val menuItemId: Int,
                      val menuItemIconId: Int,
                      val titleStringId: Int,
                      val fragment: Fragment) {
    PLANNER(R.id.bottom_navigation_item_planner, R.drawable.calendar, R.string.nav_planner_title, PlannerFragment()),
    GROCERIES(R.id.bottom_navigation_item_groceries, R.drawable.trolley, R.string.nav_groceries_title, GroceriesFragment()),
    MEALS(R.id.bottom_navigation_item_meals, R.drawable.iconlist, R.string.nav_meals_title, MealsFragment())
}

fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId) {
            return mainScreen
        }
    }
    return null
}