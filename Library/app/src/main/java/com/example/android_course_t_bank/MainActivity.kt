package com.example.android_course_t_bank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import domain.Library
import domain.LibraryObj

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryAdapter
    private val library = Library()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        // Объединение всех объектов библиотеки в один список
        val allItems = mutableListOf<LibraryObj>().apply {
            addAll(library.getBooks())
            addAll(library.getNewspapers())
            addAll(library.getDisks())
        }

        adapter = LibraryAdapter(allItems)
        recyclerView.adapter = adapter

        // ItemTouchHelper — это утилита, которая позволяет добавить функции drag & drop и swipe-to-dismiss в RecyclerView в Android
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // Перемещения элементов заблокированы
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                adapter.removeItem(position) // Удаление элемента при свайпе в любую сторону
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
