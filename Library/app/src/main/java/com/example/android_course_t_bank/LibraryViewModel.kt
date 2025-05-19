package com.example.android_course_t_bank

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import api.repository.BookResponseMapper
import api.retrofit.RetrofitHelper
import domain.Book
import domain.Disk
import domain.LibraryObj
import domain.Newspaper
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import room.LibraryDao
import room.toDomain
import room.toEntity
import kotlin.coroutines.cancellation.CancellationException


class LibraryViewModel(
    private val dao: LibraryDao,
    private var currentSortType: SortType
) : ViewModel() {

    private val _state = MutableLiveData<State<List<LibraryObj>>>(State.Loading())
    val state: LiveData<State<List<LibraryObj>>> get() = _state

    private var visibleItems: MutableList<LibraryObj> = mutableListOf() // видимые в данный момент элементы

    private var runningJob: Job? = null

    private var countAllItems = 0 // общее количество элементов в БД
    private var isLoading = false // ~ мьютекс

    private var offsetBooks = 0
    private var offsetNewspapers = 0
    private var offsetDisks = 0

    private val bookApi = RetrofitHelper.createRetrofit()

    private suspend fun getLoadItems(loadCountItems: Int = PAGE_SIZE, startInd: Int = 0, endInd: Int = PAGE_SIZE, offsetLoadBooks: Int = offsetBooks,
                  offsetLoadNewspapers: Int = offsetNewspapers, offsetLoadDisks: Int = offsetDisks): List<LibraryObj> {
        val pageLoadItems = when (currentSortType) {
            SortType.BY_NAME -> mutableListOf<LibraryObj>().apply {
                addAll(dao.getBooksSortedByName(loadCountItems, offsetLoadBooks).map { it.toDomain() })
                addAll(dao.getNewspapersSortedByName(loadCountItems, offsetLoadNewspapers).map { it.toDomain() })
                addAll(dao.getDisksSortedByName(loadCountItems, offsetLoadDisks).map { it.toDomain() })
            }
            SortType.BY_DATE -> mutableListOf<LibraryObj>().apply {
                addAll(dao.getBooksSortedByDate(loadCountItems, offsetLoadBooks).map { it.toDomain() })
                addAll(dao.getNewspapersSortedByDate(loadCountItems, offsetLoadNewspapers).map { it.toDomain() })
                addAll(dao.getDisksSortedByDate(loadCountItems, offsetLoadDisks).map { it.toDomain() })
            }
        }
        val res = sortItems(pageLoadItems, currentSortType)
        val start = startInd.coerceAtLeast(0)
        val end = endInd.coerceAtMost(res.size).coerceAtLeast(res.size)
        //println("*** ??? START=$start END=$end was_start=$startInd was_end=$endInd")
        return res.subList(start, end)
    }

    private fun loadItems() {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                //println("*** LOAD_ITEMS\n*** offsetBooks=$offsetBooks offsetNewspapers=$offsetNewspapers offsetDisks=$offsetDisks")
                _state.value = State.Loading()
                //delay(500)
                visibleItems = getLoadItems() as MutableList<LibraryObj>
                _state.value = State.Data(visibleItems.toList())

                countAllItems = dao.getCountAllItems()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    //println("*** ERROR произошла ошибка: ${e.message}")
                    _state.value = State.Error("Произошла ошибка: ${e.message}")
                }
            }
        }
    }

    fun loadNextPage(firstVisibleInd: Int, lastVisibleInd: Int) {
        if (isLoading) return
        val offsetSum = offsetBooks + offsetNewspapers + offsetDisks
        if (offsetSum == countAllItems) return // конец списка
        if (firstVisibleInd <= PREFETCH_DISTANCE) return // сдвига нет
        isLoading = true

        viewModelScope.launch {
            //println("*** LOAD_NEXT_PAGE firstVisibleInd=$firstVisibleInd lastVisibleInd=$lastVisibleInd\n*** offsetBooks=$offsetBooks offsetNewspapers=$offsetNewspapers offsetDisks=$offsetDisks")
            val prevVisibleItems = visibleItems.subList(0, firstVisibleInd - PREFETCH_DISTANCE)
            offsetBooks += prevVisibleItems.count {it is Book}
            offsetNewspapers += prevVisibleItems.count {it is Newspaper}
            offsetDisks += prevVisibleItems.count {it is Disk}
            loadItems()
            isLoading = false
        }
    }

    fun loadPreviousPage(firstVisibleInd: Int, lastVisibleInd: Int) {
        if (isLoading) return
        if (firstVisibleInd >= PREFETCH_DISTANCE) return // начало списка
        if (lastVisibleInd + PREFETCH_DISTANCE >= PAGE_SIZE) return // сдвига нет
        isLoading = true

        viewModelScope.launch {
            //println("*** LOAD_PREV_PAGE firstVisibleInd=$firstVisibleInd lastVisibleInd=$lastVisibleInd\n*** offsetBooks=$offsetBooks offsetNewspapers=$offsetNewspapers offsetDisks=$offsetDisks")
            val newOffsetBooks = (offsetBooks - PAGE_SIZE).coerceAtLeast(0)
            val newOffsetNewspapers = (offsetNewspapers - PAGE_SIZE).coerceAtLeast(0)
            val newOffsetDisks = (offsetDisks - PAGE_SIZE).coerceAtLeast(0)
            val loadCountItems = (offsetBooks - newOffsetBooks) + (offsetNewspapers - newOffsetNewspapers) + (offsetDisks - newOffsetDisks)
            val countVisibleItems = lastVisibleInd - firstVisibleInd + 1
            val prevItems = getLoadItems(loadCountItems, loadCountItems - PAGE_SIZE, loadCountItems - countVisibleItems, newOffsetBooks, newOffsetNewspapers, newOffsetDisks)
            //println("*** ??? newOffsetBooks=$newOffsetBooks, newOffsetNewspapers=$newOffsetNewspapers, newOffsetDisks=$newOffsetDisks, loadCountItems=$loadCountItems, countVisibleItems=$countVisibleItems, prevItems=$prevItems")
            offsetBooks -= prevItems.count { it is Book }
            offsetNewspapers -= prevItems.count { it is Newspaper }
            offsetDisks -= prevItems.count { it is Disk }
            loadItems()
            isLoading = false
        }
    }

    fun addItem(obj: LibraryObj, isLoadItems: Boolean = true) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                when (obj) {
                    is Book -> dao.insertBook(obj.toEntity())
                    is Newspaper -> dao.insertNewspaper(obj.toEntity())
                    is Disk -> dao.insertDisk(obj.toEntity())
                }
                if (isLoadItems) loadItems()
            } catch (e: Exception) {
                _state.value = State.Error("Произошла ошибка при добавлении: ${e.message}")
            }
        }
    }

    fun removeItem(obj: LibraryObj, onError: (() -> Unit)? = null) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                when (obj) {
                    is Book -> dao.deleteBook(obj.toEntity())
                    is Newspaper -> dao.deleteNewspaper(obj.toEntity())
                    is Disk -> dao.deleteDisk(obj.toEntity())
                }
                loadItems()
            } catch (e: Exception) {
                _state.value = State.Error("Произошла ошибка при удалении: ${e.message}")
                onError?.invoke()
            }
        }
    }

    fun loadInitialItems() {
        offsetBooks = 0
        offsetDisks = 0
        offsetNewspapers = 0
        loadItems()
    }

    fun sortCurrentItems(sortType: SortType) {
        currentSortType = sortType
        loadInitialItems()
    }

    fun searchGoogleBooks(query: String) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                _state.value = State.Loading()

                val response = bookApi.searchBooks(query)
                val books = response.items?.map { BookResponseMapper.map(it) } ?: emptyList()

                _state.value = State.Data(books)
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("GoogleBooks", "Ошибка поиска", e)
                    _state.value = State.Error("Ошибка поиска: ${e.message}")
                }
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        const val PAGE_SIZE = 15 // количество элементов, загружаемых в память для отображения
        const val PREFETCH_DISTANCE = 5
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