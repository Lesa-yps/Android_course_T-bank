package domain

class  Book(id: Int, isAvailable: Boolean, name: String, val pages: Int, val author: String, addedDate: Long? = null) :
        LibraryObj(id, isAvailable, name, addedDate), LibraryReadableHere, LibraryTakableHome, LibraryDigitizable {

    override val humanReadableType = "Книга"

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("Книга: $name ($pages страниц) автор: $author с id: $id доступна: $isAvailableYN")
    }

    override fun getLongInfo(): String {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        return "Книга: $name\n $pages страниц\n автор: $author\n id: $id\n доступна: $isAvailableYN"
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