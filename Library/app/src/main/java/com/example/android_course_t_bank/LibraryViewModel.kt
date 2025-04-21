package com.example.android_course_t_bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.Library
import domain.LibraryObj
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val _state = MutableLiveData<State<List<LibraryObj>>>(State.Loading())
    val state: LiveData<State<List<LibraryObj>>> get() = _state

    private var runningJob: Job? = null

    var countDataAsk = 0
    private val lastSuccessfulList = mutableListOf<LibraryObj>()

    fun loadItems(library: Library) {
        //println("LOAD_ITEMS")
        runningJob?.cancel() // отмена предыдущей загрузки при наличии
        runningJob = viewModelScope.launch {
            try {
                // установка состояния загрузки
                _state.value = State.Loading()
                // симуляция задержки
                delay(1000)
                // получение данных
                val allItems = mutableListOf<LibraryObj>().apply {
                    addAll(library.getBooks())
                    addAll(library.getNewspapers())
                    addAll(library.getDisks())
                }
                // инкремент и рандомизированная ошибка на каждое 5-е обращение
                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка загрузки данных.")
                }
                // установка состояния с данными
                _state.value = State.Data(allItems)
                lastSuccessfulList.clear()
                lastSuccessfulList.addAll(allItems)
            } catch (e: Exception) {
                // установка состояния ошибки
                _state.value = State.Error("Произошла ошибка: ${e.message}")
            }
        }
    }

    fun addItem(obj: LibraryObj) {
        //println("ADD_ITEMS")
        runningJob?.cancel() // отмена предыдущей загрузки при наличии
        runningJob = viewModelScope.launch {
            try {
                // установка состояния загрузки
                _state.value = State.Loading()
                // симуляция задержки
                delay((100..2000).random().toLong())
                // инкремент и рандомизированная ошибка на каждое 5-е обращение
                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка добавления элемента.")
                }
                // добавление нового элемента в список
                lastSuccessfulList.add(obj)
                _state.value = State.Data(lastSuccessfulList.toList())
            } catch (e: Exception) {
                // установка состояния ошибки
                _state.value = State.Error("Произошла ошибка при добавлении: ${e.message}")
            }
        }
    }

    fun removeItem(position: Int, onError: (() -> Unit)? = null) {
        //println("REMOVE_ITEMS")
        runningJob?.cancel() // отмена предыдущей загрузки при наличии
        runningJob = viewModelScope.launch {
            try {
                // установка состояния загрузки
                _state.value = State.Loading()
                // симуляция задержки
                delay((100..2000).random().toLong())
                // инкремент и рандомизированная ошибка на каждое 5-е обращение
                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка удаления элемента.")
                }
                // удаление элемента из списка
                lastSuccessfulList.removeAt(position)
                _state.value = State.Data(lastSuccessfulList.toList())
            } catch (e: Exception) {
                //println("ERROR!")
                // установка состояния ошибки
                _state.value = State.Error("Произошла ошибка при удалении: ${e.message}")
                onError?.invoke()
            }
        }
    }

    fun refreshFromLastSuccessful() {
        runningJob?.cancel() // отмена предыдущей загрузки при наличии
        runningJob = viewModelScope.launch {
            try {
                // установка состояния загрузки
                _state.value = State.Loading()
                // симуляция задержки
                delay((100..2000).random().toLong())
                // инкремент и рандомизированная ошибка на каждое 5-е обращение
                countDataAsk++
                if (countDataAsk % 5 == 0) {
                    throw Exception("Симулированная ошибка обновления.")
                }
                // обновление списка
                _state.value = State.Data(lastSuccessfulList.toList())
            } catch (e: Exception) {
                // установка состояния ошибки
                _state.value = State.Error("Произошла ошибка при обновлении: ${e.message}")
            }
        }
    }
}