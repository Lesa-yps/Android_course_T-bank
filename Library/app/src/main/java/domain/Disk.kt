package domain

import java.io.Serializable

enum class DiskType : Serializable { CD, DVD }

class Disk(id: Int, isAvailable: Boolean, name: String, val type: DiskType) :
        LibraryObj(id, isAvailable, name), LibraryTakableHome {

    override val humanReadableType = "Диск"

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("$type $name доступен: $isAvailableYN")
    }

    override fun getLongInfo(): String {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        return "Диск $name\n тип $type\n доступен: $isAvailableYN"
    }

    override fun takeHome() {
        if (!isAvailable) println("Ошибка: Этот диск уже занят.")
        else {
            isAvailable = false
            println("Диск id=$id взят домой.")
        }
    }
}