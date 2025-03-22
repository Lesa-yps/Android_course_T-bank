package domain

abstract class LibraryObj(protected val id: Int, protected var isAvailable: Boolean, protected val name: String) {

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

    abstract fun showLongInfo()
}