package com.example.android_course_t_bank

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import domain.Book
import domain.Disk
import domain.LibraryObj
import domain.Newspaper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import room.LibraryDao
import room.toDomain
import room.toEntity

class LibraryViewModel(
    private val dao: LibraryDao,
    private var currentSortType: SortType
) : ViewModel() {

    private val _state = MutableLiveData<State<List<LibraryObj>>>(State.Loading())
    val state: LiveData<State<List<LibraryObj>>> get() = _state

    private var allItems: List<LibraryObj> = emptyList()
    private var visibleItems: MutableList<LibraryObj> = mutableListOf()

    private var runningJob: Job? = null
    private var countDataAsk = 0

    // параметры пагинации
    private val initialLoadCount = 30
    private val pageLoadCount = 15

    private var startIndex = 0
    private var isLoading = false // ~ мьютекс

    fun loadInitialItems() {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                _state.value = State.Loading()
                delay(500)

                allItems = mutableListOf<LibraryObj>().apply {
                    addAll(dao.getAllBooks().map { it.toDomain() })
                    addAll(dao.getAllNewspapers().map { it.toDomain() })
                    addAll(dao.getAllDisks().map { it.toDomain() })
                }
                println(allItems)
                allItems = sortItems(allItems, currentSortType)
                println(allItems)

                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка загрузки данных.")
                }

                startIndex = 0
                visibleItems = allItems.take(initialLoadCount).toMutableList()
                _state.value = State.Data(visibleItems.toList())

            } catch (e: Exception) {
                _state.value = State.Error("Произошла ошибка: ${e.message}")
            }
        }
    }

    fun loadNextPage() {
        if (isLoading) return
        isLoading = true
        if (startIndex + visibleItems.size >= allItems.size) return // конец списка

        viewModelScope.launch {
            val nextItems = allItems.drop(startIndex + visibleItems.size).take(pageLoadCount)
            visibleItems.addAll(nextItems)
            val removeCount = minOf(pageLoadCount, visibleItems.size - initialLoadCount)
            repeat(removeCount) { visibleItems.removeAt(0) }
            startIndex += removeCount

            _state.value = State.Data(visibleItems.toList())
        }
        isLoading = false
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun loadPreviousPage() {
        if (isLoading) return
        isLoading = true
        if (startIndex == 0) return // начало списка

        viewModelScope.launch {
            val prevStart = maxOf(startIndex - pageLoadCount, 0)
            val prevItems = allItems.subList(prevStart, startIndex)
            visibleItems.addAll(0, prevItems)
            val removeCount = minOf(pageLoadCount, visibleItems.size - initialLoadCount)
            repeat(removeCount) { visibleItems.removeLast() }
            startIndex = prevStart

            _state.value = State.Data(visibleItems.toList())
        }
        isLoading = false
    }

    fun addItem(obj: LibraryObj) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                _state.value = State.Loading()
                delay((100..2000).random().toLong())

                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка добавления элемента.")
                }

                when (obj) {
                    is Book -> dao.insertBook(obj.toEntity())
                    is Newspaper -> dao.insertNewspaper(obj.toEntity())
                    is Disk -> dao.insertDisk(obj.toEntity())
                }

                loadInitialItems()
            } catch (e: Exception) {
                _state.value = State.Error("Произошла ошибка при добавлении: ${e.message}")
            }
        }
    }

    fun removeItem(obj: LibraryObj, onError: (() -> Unit)? = null) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                _state.value = State.Loading()
                delay((100..2000).random().toLong())

                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка удаления элемента.")
                }

                when (obj) {
                    is Book -> dao.deleteBook(obj.toEntity())
                    is Newspaper -> dao.deleteNewspaper(obj.toEntity())
                    is Disk -> dao.deleteDisk(obj.toEntity())
                }

                loadInitialItems()
            } catch (e: Exception) {
                _state.value = State.Error("Произошла ошибка при удалении: ${e.message}")
                onError?.invoke()
            }
        }
    }

    fun sortCurrentItems(sortType: SortType) {
        currentSortType = sortType
        loadInitialItems()
    }
}

class LibraryViewModelFactory(
    private val dao: LibraryDao,
    private val initialSortType: SortType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(dao, initialSortType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}