package com.example.android_course_t_bank

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import domain.Library
import domain.LibraryObj


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val library = Library()
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)


        val allItems = mutableListOf<LibraryObj>().apply {
            addAll(library.getBooks())
            addAll(library.getNewspapers())
            addAll(library.getDisks())
        }

        val adapter = LibraryAdapter(allItems)
        recyclerView.adapter = adapter

        viewModel.items.observe(this) {
            adapter.setItems(it)
        }
        viewModel.loadItems(library)

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

        // Обработка кнопки "Добавить" (элемент)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)

        buttonAdd.setOnClickListener {
            val intent = DetailActivity.createIntent(this, null, isReadOnly = false)
            addItemLauncher.launch(intent)
        }

    }

    private val addItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val newObj: LibraryObj? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(DetailActivity.LIB_OBJ, LibraryObj::class.java)
            } else {
                @Suppress("DEPRECATION") it.data?.getSerializableExtra(DetailActivity.LIB_OBJ) as? LibraryObj
            }
            newObj?.let { obj ->
                viewModel.addItem(obj)
            }
        }
    }

}
