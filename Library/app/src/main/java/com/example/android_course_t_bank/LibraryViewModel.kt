package com.example.android_course_t_bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import domain.Library
import domain.LibraryObj

class LibraryViewModel : ViewModel() {
    private val _items = MutableLiveData<List<LibraryObj>>(emptyList())
    val items: LiveData<List<LibraryObj>> get() = _items

    // Объединение всех объектов библиотеки в один список
    fun loadItems(library: Library) {
        val allItems = mutableListOf<LibraryObj>().apply {
            addAll(library.getBooks())
            addAll(library.getNewspapers())
            addAll(library.getDisks())
        }
        _items.value = allItems
    }

    fun addItem(obj: LibraryObj) {
        val current = _items.value?.toMutableList() ?: mutableListOf()
        current.add(obj)
        _items.value = current
    }

    fun removeItem(position: Int) {
        val current = _items.value?.toMutableList() ?: return
        current.removeAt(position)
        _items.value = current
    }
}