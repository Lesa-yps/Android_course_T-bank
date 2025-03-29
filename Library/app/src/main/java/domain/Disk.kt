package domain

enum class DiskType { CD, DVD }

class Disk(id: Int, isAvailable: Boolean, name: String, private val type: DiskType) :
        LibraryObj(id, isAvailable, name), LibraryTakableHome {

    override val humanReadableType = "Диск"

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("$type $name доступен: $isAvailableYN")
    }

    override fun takeHome() {
        if (!isAvailable) println("Ошибка: Этот диск уже занят.")
        else {
            isAvailable = false
            println("Диск id=$id взят домой.")
        }
    }
}