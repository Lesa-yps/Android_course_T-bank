package com.example.android_course_t_bank

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import api.retrofit.RetrofitHelper
import di.LibraryComponentProvider
import domain.Book
import repository.LibraryRepositoryImpl
import room.MIGRATION_1_2
import utils.SortType
import utils.getSavedSortType
import utils.saveSortType
import viewmodel.LibraryViewModel
import viewmodel.State
import javax.inject.Inject


const val MIN_COUNT_LETTERS_TO_SEARCH = 3

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var errorTextView: TextView
    private lateinit var buttonAddUpdate: Button
    private lateinit var adapter: LibraryAdapter
    private lateinit var sortSpinner: Spinner
    private lateinit var adapterSpinner: ArrayAdapter<String>

    @Inject lateinit var viewModelAssistedFactory: LibraryViewModel.Factory
    lateinit var viewModel: LibraryViewModel

    private lateinit var btnLocalLibrary: Button
    private lateinit var btnGoogleBooks: Button
    private lateinit var searchForm: LinearLayout
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var btnSearch: Button

    private var isGoogleBooksMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as LibraryComponentProvider)
            .getLibraryComponent()
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentSortType = getSavedSortType(this)
        viewModel = viewModelAssistedFactory.create(currentSortType)

        recyclerView = findViewById(R.id.recyclerView)
        shimmerLayout = findViewById(R.id.shimmerLayout)
        errorTextView = findViewById(R.id.errorTextView)

        buttonAddUpdate = findViewById<Button>(R.id.buttonAddUpdate)

        btnLocalLibrary = findViewById(R.id.btnLocalLibrary)
        btnGoogleBooks = findViewById(R.id.btnGoogleBooks)
        searchForm = findViewById(R.id.searchForm)
        etTitle = findViewById(R.id.etTitle)
        etAuthor = findViewById(R.id.etAuthor)
        btnSearch = findViewById(R.id.btnSearch)

        // обработчики переключения режимов
        btnLocalLibrary.setOnClickListener { switchToLocalLibrary() }
        btnGoogleBooks.setOnClickListener { switchToGoogleBooks() }

        // проверка активности кнопки поиска
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val titleLength = etTitle.text.length
                val authorLength = etAuthor.text.length
                btnSearch.isEnabled = titleLength >= MIN_COUNT_LETTERS_TO_SEARCH || authorLength >= MIN_COUNT_LETTERS_TO_SEARCH
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etTitle.addTextChangedListener(textWatcher)
        etAuthor.addTextChangedListener(textWatcher)

        btnSearch.setOnClickListener { performGoogleBooksSearch() }

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

        adapter = LibraryAdapter(
            mutableListOf(),
            onItemClick = { selectedObj ->
                if (!isGoogleBooksMode) {
                    val intent = DetailActivity.createIntent(this, selectedObj, isReadOnly = true)
                    addItemLauncher.launch(intent)
                }
            },
            onItemLongClick = { selectedObj ->
                if (isGoogleBooksMode && selectedObj is Book) {
                    viewModel.addItem(selectedObj, false)
                    Toast.makeText(this, "Книга добавлена в библиотеку", Toast.LENGTH_SHORT).show()
                }
            }
        )
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
                if (isGoogleBooksMode) {
                    adapter.notifyItemChanged(position)
                    return
                }

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
                if (isGoogleBooksMode) return

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (dy > 0) {
                    // пользователь скроллит вниз, подгружается следующая страница
                    if (lastVisibleItem >= totalItemCount - LibraryViewModel.PREFETCH_DISTANCE) {
                        //println("*** SCROLL ↓ last=$lastVisibleItem / total=$totalItemCount")
                        viewModel.loadNextPage(firstVisibleItem)
                    }
                } else if (dy < 0) {
                    // пользователь скроллит вверх, подгружается предыдущая страница
                    if (firstVisibleItem <= LibraryViewModel.PREFETCH_DISTANCE) {
                        //println("*** SCROLL ↑ first=$firstVisibleItem / total=$totalItemCount")
                        viewModel.loadPreviousPage(firstVisibleItem, lastVisibleItem)
                    }
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
                recyclerView.scrollToPosition(0)
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

    private fun switchToLocalLibrary() {
        isGoogleBooksMode = false
        btnLocalLibrary.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        btnGoogleBooks.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        searchForm.visibility = View.GONE
        sortSpinner.visibility = View.VISIBLE
        viewModel.loadInitialItems()
        buttonAddUpdate.visibility = View.VISIBLE
    }

    private fun switchToGoogleBooks() {
        isGoogleBooksMode = true
        btnLocalLibrary.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        btnGoogleBooks.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        sortSpinner.visibility = View.GONE
        searchForm.visibility = View.VISIBLE
        adapter.setItems(emptyList())
        buttonAddUpdate.visibility = View.GONE
    }

    private fun performGoogleBooksSearch() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()

        val query = buildString {
            if (title.isNotEmpty()) append("intitle:$title ")
            if (author.isNotEmpty()) append("inauthor:$author")
        }.trim()

        if (query.isEmpty()) return

        viewModel.searchGoogleBooks(query)
    }
}