package domain

import kotlin.random.Random


class DigitizationOffice<in T: LibraryDigitizable> {

    fun digitize(obj: T): Disk {
        val id = Random.nextInt(100, 1000)
        val isAvailable = true
        val name = obj.digitizableName()
        val type = DiskType.CD

        println("Объект оцифрован.")
        return Disk(id, isAvailable, name, type)
    }
}