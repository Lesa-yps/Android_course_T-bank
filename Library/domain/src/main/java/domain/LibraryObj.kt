package domain

import java.io.Serializable

abstract class LibraryObj(val id: Int, var isAvailable: Boolean, val name: String, val addedDate: Long? = null): Serializable {

    abstract val humanReadableType: String

    fun showShortInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("$name доступность: $isAvailableYN")
    }

    fun returnObj() {
        if (isAvailable)
            println("Ошибка: Этот объект уже в библиотеке, его нельзя вернуть.")
        else {
            isAvailable = true
            println("Объект $humanReadableType с id=$id возвращён.")
        }
    }

    fun myGetId(): Int = id
    fun myGetName(): String = name
    fun myGetIsAvailable(): Boolean = isAvailable

    abstract fun showLongInfo()
    abstract fun getLongInfo(): String
}