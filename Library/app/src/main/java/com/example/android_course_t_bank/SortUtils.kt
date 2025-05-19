package com.example.android_course_t_bank

import android.content.Context
import androidx.core.content.edit
import domain.LibraryObj

const val PREFS_NAME = "sort_prefs"
const val KEY_SORT_TYPE = "sort_type"

enum class SortType {
    BY_NAME,
    BY_DATE
}

fun saveSortType(context: Context, sortType: SortType) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit() { putString(KEY_SORT_TYPE, sortType.name) }
}

fun getSavedSortType(context: Context): SortType {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val sortTypeName = prefs.getString(KEY_SORT_TYPE, SortType.BY_NAME.name)
    return SortType.valueOf(sortTypeName ?: SortType.BY_NAME.name)
}

fun sortItems(items: List<LibraryObj>, sortType: SortType): List<LibraryObj> {
    return when (sortType) {
        SortType.BY_NAME -> items.sortedBy { it.name }
        SortType.BY_DATE -> items.sortedBy { it.addedDate }
    }
}