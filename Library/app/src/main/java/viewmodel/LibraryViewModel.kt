package viewmodel

import android.util.Log
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
import kotlinx.coroutines.launch
import utils.SortType
import kotlin.coroutines.cancellation.CancellationException
import repository.LibraryRepository
import utils.sortItemsComparator


class LibraryViewModel(
    private val repository: LibraryRepository,
    private var currentSortType: SortType
) : ViewModel() {

    private val _state = MutableLiveData<State<List<LibraryObj>>>(State.Loading())
    val state: LiveData<State<List<LibraryObj>>> get() = _state

    private var visibleItems: MutableList<LibraryObj> = mutableListOf()

    private var runningJob: Job? = null

    private var countAllItems = 0 // общее количество элементов в БД
    private var isLoading = false // ~ мьютекс

    private var offsetBooks = 0
    private var offsetNewspapers = 0
    private var offsetDisks = 0

    // загружаем элементы с пагинацией
    private suspend fun getLoadItems(loadCountItems: Int = PAGE_SIZE, startInd: Int = 0, endInd: Int = PAGE_SIZE, offsetLoadBooks: Int = offsetBooks,
                                     offsetLoadNewspapers: Int = offsetNewspapers, offsetLoadDisks: Int = offsetDisks): List<LibraryObj> {
        val pageLoadItems = repository.getItemsSorted(currentSortType, offsetLoadBooks, offsetLoadNewspapers, offsetLoadDisks, loadCountItems)
        val res = pageLoadItems.sortedWith(sortItemsComparator(currentSortType))
        val start = startInd.coerceAtLeast(0)
        val end = endInd.coerceAtMost(res.size).coerceAtLeast(res.size)
        return res.subList(start, end)
    }

    private fun loadItems() {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            try {
                _state.value = State.Loading()
                visibleItems = getLoadItems().toMutableList()
                _state.value = State.Data(visibleItems.toList())

                countAllItems = repository.getItemCount() // получаем общее количество элементов
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _state.value = State.Error("Произошла ошибка: ${e.message}")
                }
            }
        }
    }

    fun loadNextPage(firstVisibleInd: Int) {
        if (isLoading) return
        val offsetSum = offsetBooks + offsetNewspapers + offsetDisks
        if (offsetSum == countAllItems) return // конец списка
        if (firstVisibleInd <= PREFETCH_DISTANCE) return // сдвига нет
        isLoading = true

        viewModelScope.launch {
            val prevVisibleItems = visibleItems.subList(0, firstVisibleInd - PREFETCH_DISTANCE)
            offsetBooks += prevVisibleItems.count { it is Book }
            offsetNewspapers += prevVisibleItems.count { it is Newspaper }
            offsetDisks += prevVisibleItems.count { it is Disk }
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
            val newOffsetBooks = (offsetBooks - PAGE_SIZE).coerceAtLeast(0)
            val newOffsetNewspapers = (offsetNewspapers - PAGE_SIZE).coerceAtLeast(0)
            val newOffsetDisks = (offsetDisks - PAGE_SIZE).coerceAtLeast(0)
            val loadCountItems = (offsetBooks - newOffsetBooks) + (offsetNewspapers - newOffsetNewspapers) +
                    (offsetDisks - newOffsetDisks)
            val countVisibleItems = lastVisibleInd - firstVisibleInd + 1
            val prevItems = getLoadItems(loadCountItems, loadCountItems - PAGE_SIZE,
                loadCountItems - countVisibleItems, newOffsetBooks, newOffsetNewspapers, newOffsetDisks)
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
                repository.addItem(obj) // Взаимодействуем с репозиторием
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
                repository.removeItem(obj)
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

                val books = repository.searchGoogleBooks(query)
                _state.value = State.Data(books)
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("GoogleBooks", "Ошибка поиска", e)
                    _state.value = State.Error("Ошибка поиска: ${e.message}")
                }
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 15 // количество элементов, загружаемых в память
        const val PREFETCH_DISTANCE = 5
    }
}

class LibraryViewModelFactory(
    private val repository: LibraryRepository,
    private val initialSortType: SortType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(repository, initialSortType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}