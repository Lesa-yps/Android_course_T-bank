package domain

import java.io.Serializable


class Newspaper(id: Int, isAvailable: Boolean, name: String, val issueNumber: Int, val month: Int, addedDate: Long? = null) :
        LibraryObj(id, isAvailable, name, addedDate), LibraryReadableHere, LibraryDigitizable {

    override val humanReadableType = "Газета"

    init {
        require(month in 1..12) { "Ошибка: Некорректный месяц выпуска ($month). Должен быть от 1 до 12." }
    }

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        val monthName = MonthRu.fromNumber(month)
        println("Газета: $name, выпуск $issueNumber, id: $id, месяц: ${monthName}, доступна: $isAvailableYN")
    }

    override fun getLongInfo(): String {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        val monthName = MonthRu.fromNumber(month)
        return "Газета: $name\n выпуск $issueNumber\n id: $id\n месяц: ${monthName}\n доступна: $isAvailableYN"
    }

    override fun readHere() {
        if (!isAvailable) println("Ошибка: Эта газета уже занята.")
        else {
            isAvailable = false
            println("Газета id=$id взята в читальный зал.")
        }
    }

    override fun digitizableName(): String {
        return "Цифровая копия: $name"
    }
}


enum class MonthRu(val number: Int, val monthRusName: String) : Serializable {
    JANUARY(1, "Январь"),
    FEBRUARY(2, "Февраль"),
    MARCH(3, "Март"),
    APRIL(4, "Апрель"),
    MAY(5, "Май"),
    JUNE(6, "Июнь"),
    JULY(7, "Июль"),
    AUGUST(8, "Август"),
    SEPTEMBER(9, "Сентябрь"),
    OCTOBER(10, "Октябрь"),
    NOVEMBER(11, "Ноябрь"),
    DECEMBER(12, "Декабрь");

    companion object {
        fun fromNumber(number: Int): String {
            return entries.find { it.number == number } ?.monthRusName ?: "Неизвестный месяц"
        }
    }
}
