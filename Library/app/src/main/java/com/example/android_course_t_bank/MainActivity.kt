package com.example.android_course_t_bank

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.facebook.shimmer.ShimmerFrameLayout
import domain.LibraryObj
import room.LibraryDao
import room.LibraryDatabase
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var errorTextView: TextView
    private lateinit var buttonAddUpdate: Button
    private lateinit var viewModel: LibraryViewModel
    private lateinit var adapter: LibraryAdapter
    private lateinit var sortSpinner: Spinner
    private lateinit var adapterSpinner: ArrayAdapter<String>

    private lateinit var db: LibraryDatabase
    private lateinit var dao: LibraryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // инициализация базы данных
        db = Room.databaseBuilder(
            applicationContext,
            LibraryDatabase::class.java, "library.db"
        ).build()
        dao = db.libraryDao()

        // Создание ViewModel через фабрику
        val currentSortType = getSavedSortType(this)
        val factory = LibraryViewModelFactory(dao, currentSortType)
        viewModel = ViewModelProvider(this, factory)[LibraryViewModel::class.java]

        recyclerView = findViewById(R.id.recyclerView)
        shimmerLayout = findViewById(R.id.shimmerLayout)
        errorTextView = findViewById(R.id.errorTextView)

        buttonAddUpdate = findViewById<Button>(R.id.buttonAddUpdate)

        sortSpinner = findViewById<Spinner>(R.id.sortSpinner)
        val sortOptions = listOf("сортировка по наименованию", "сортировка по дате добавления")
        adapterSpinner = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortOptions)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapterSpinner
        sortSpinner.setSelection(
            when (currentSortType) {
                SortType.BY_NAME -> 0
                SortType.BY_DATE -> 1
            }
        )

        adapter = LibraryAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.state.observe(this) { state ->
            when (state) {
                is State.Loading -> {
                    //println("LOADING")
                    shimmerLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmer()
                    errorTextView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    buttonAddUpdate.isEnabled = false
                    buttonAddUpdate.text = "Загрузка..."
                }

                is State.Error -> {
                    //println("ERROR")
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = state.message
                    recyclerView.visibility = View.GONE
                    buttonAddUpdate.isEnabled = true
                    buttonAddUpdate.text = "Обновить"
                }

                is State.Data<*> -> {
                    //println("DATA")
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    errorTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.setItems(state.data as List<LibraryObj>)
                    buttonAddUpdate.isEnabled = true
                    buttonAddUpdate.text = "Добавить"
                }
            }
        }

        viewModel.loadInitialItems()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val swipedObject = adapter.getItem(position)
                viewModel.removeItem(
                    obj = swipedObject,
                    onError = {
                        // возврат элемента в адаптер
                        adapter.notifyItemChanged(position)
                    }
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // подгрузка следующих стр, когда долистали почти до конца
                if (lastVisibleItem >= totalItemCount - 5) {
                    viewModel.loadNextPage()
                }

                // подгрузка предыдущих страниц, когда листаем вверх
                if (firstVisibleItem <= 5) {
                    viewModel.loadPreviousPage()
                }
            }
        })

        buttonAddUpdate.setOnClickListener {
            when (viewModel.state.value) {
                is State.Error -> {
                    viewModel.loadInitialItems()
                }
                else -> {
                    val intent = DetailActivity.createIntent(this, null, isReadOnly = false)
                    addItemLauncher.launch(intent)
                }
            }
        }

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSortType = when (position) {
                    0 -> SortType.BY_NAME
                    else -> SortType.BY_DATE
                }
                saveSortType(this@MainActivity, selectedSortType)
                viewModel.sortCurrentItems(selectedSortType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private val addItemLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val newObj: LibraryObj? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra(DetailActivity.LIB_OBJ, LibraryObj::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra(DetailActivity.LIB_OBJ) as? LibraryObj
            }
            newObj?.let { obj -> viewModel.addItem(obj) }
        }
    }
}
