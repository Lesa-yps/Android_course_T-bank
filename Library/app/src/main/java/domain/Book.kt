package domain

class  Book(id: Int, isAvailable: Boolean, name: String, private val pages: Int, private val author: String) :
        LibraryObj(id, isAvailable, name), LibraryReadableHere, LibraryTakableHome, LibraryDigitizable {

    override val humanReadableType = "Книга"

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("Книга: $name ($pages страниц) автор: $author с id: $id доступна: $isAvailableYN")
    }

    override fun takeHome() {
        if (!isAvailable) println("Ошибка: Эта книга уже занята.")
        else {
            isAvailable = false
            println("Книга id=$id взята домой.")
        }
    }

    override fun readHere() {
        if (!isAvailable) println("Ошибка: Эта книга уже занята.")
        else {
            isAvailable = false
            println("Книга id=$id взята в читальный зал.")
        }
    }

    override fun digitizableName(): String {
        return "Цифровая копия: $name"
    }
}