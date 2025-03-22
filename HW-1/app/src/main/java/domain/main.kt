package domain

fun main() {
    println("Добро пожаловать, читатель!")

    val library = Library()

    while (true) {

        // первое (главное) меню (выбор типа объекта)
        println("\n   Главное меню:\n" +
                "1. Показать книги\n" +
                "2. Показать газеты\n" +
                "3. Показать диски\n" +
                "4. Выйти из программы\n")
        print("Выберите пункт меню: ")
        val userChooseType = readlnOrNull()?.toIntOrNull()
        if (userChooseType == 4) {
            println("Выход из программы...")
            break
        } else if (userChooseType !in 1..3) {
            println("Неверный выбор. Пожалуйста, попробуйте снова.")
            continue
        }

        do {
            library.showShortInfoObjs(userChooseType!!)

            var flagGoToMainMenu = false

            // второе меню (выбор объекта)
            print("Выберите номер объекта (или 0 для возврата в главное меню): ")
            var userChooseObj = readlnOrNull()?.toIntOrNull()
            val countChooseObj = library.getCountObj(userChooseType)
            while (userChooseObj !in 0..countChooseObj) {
                print("\nОшибка! Пожалуйста, попробуйте снова: ")
                userChooseObj = readlnOrNull()?.toIntOrNull()
            }
            if (userChooseObj == 0) break
            userChooseObj = userChooseObj!!.minus(1)

            // третье меню (выбор действия с объектом)
            println("\n   Меню:\n" +
                    "1. Взять домой\n" +
                    "2. Читать в читальном зале\n" +
                    "3. Показать детальную информацию\n" +
                    "4. Вернуть\n" +
                    "5. Вернуться в главное меню\n")
            print("Выберите пункт меню: ")

            do {
                val userAction = readlnOrNull()?.toIntOrNull()
                when (userAction) {
                    1 -> library.takeHome(userChooseType, userChooseObj)
                    2 -> library.takeRead(userChooseType, userChooseObj)
                    3 -> library.showLongInfoObj(userChooseType, userChooseObj)
                    4 -> library.returnObj(userChooseType, userChooseObj)
                    5 -> flagGoToMainMenu = true
                    else -> println("Неверный выбор. Пожалуйста, попробуйте снова.")
                }
            } while (userAction !in 1..5)

        } while (!flagGoToMainMenu)
    }
}

open class LibraryObj(protected val id: Int, protected var isAvailable: Boolean, protected val name: String) {
    open fun showShortInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("$name доступен: $isAvailableYN")
    }
    open fun showLongInfo() {
        println("Ошибка: Подробная информация недоступна.")
    }
    open fun takeHome() {
        println("Ошибка: Этот объект нельзя взять домой.")
    }
    open fun takeRead() {
        println("Ошибка: Этот объект нельзя читать в читальном зале.")
    }
    open fun returnObj() {
        println("Ошибка: Этот объект нельзя вернуть.")
    }
}

class Book(id: Int, isAvailable: Boolean, name: String, private val pages: Int, private val author: String) : LibraryObj(id, isAvailable, name) {
    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("Книга: $name ($pages страниц) автор: $author с id: $id доступна: $isAvailableYN")
    }
    override fun takeHome() {
        if (!isAvailable) println("Ошибка: Эта книга уже занята.")
        else {
            isAvailable = false
            println("Книга $id взята домой.")
        }
    }
    override fun takeRead() {
        if (!isAvailable) println("Ошибка: Эта книга уже занята.")
        else {
            isAvailable = false
            println("Книга $id взята в читальный зал.")
        }
    }
    override fun returnObj() {
        if (isAvailable) println("Ошибка: Эта книга уже в библиотеке, ее нельзя вернуть.")
        else {
            isAvailable = true
            println("Книга $id возвращена.")
        }
    }
}

class Newspaper(id: Int, isAvailable: Boolean, name: String, private val issueNumber: Int) : LibraryObj(id, isAvailable, name) {
    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("Газета: $name, выпуск $issueNumber, id: $id доступна: $isAvailableYN")
    }
    override fun takeRead() {
        if (!isAvailable) println("Ошибка: Эта газета уже занята.")
        else {
            isAvailable = false
            println("Газета $id взята в читальный зал.")
        }
    }
    override fun returnObj() {
        if (isAvailable) println("Ошибка: Эта газета уже в библиотеке, ее нельзя вернуть.")
        else {
            isAvailable = true
            println("Газета $id возвращена.")
        }
    }
}

enum class DiskType { CD, DVD }

class Disk(id: Int, isAvailable: Boolean, name: String, private val type: DiskType) : LibraryObj(id, isAvailable, name) {
    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Да" else "Нет"
        println("$type $name доступен: $isAvailableYN")
    }
    override fun takeHome() {
        if (!isAvailable) println("Ошибка: Этот диск уже занят.")
        else {
            isAvailable = false
            println("Диск $id взят домой.")
        }
    }
    override fun returnObj() {
        if (isAvailable) println("Ошибка: Этот диск уже в библиотеке, его нельзя вернуть.")
        else {
            isAvailable = true
            println("Диск $id возвращен.")
        }
    }
}

class Library {
    private val lstBooks = listOf(
        Book(1, true, "Маугли", 202, "Редьярд Киплинг"),
        Book(2, true, "Война и мир", 1225, "Лев Толстой")
    )
    private val lstNewspapers = listOf(
        Newspaper(3, false, "Деревенская жизнь", 794),
        Newspaper(4, true, "Комсомольская правда", 1123)
    )
    private val lstDisks = listOf(
        Disk(5, true, "Дэдпул и Росомаха", DiskType.DVD),
        Disk(6, false, "Ледниковый период", DiskType.CD)
    )
    private val allObjs: Map<Int, List<LibraryObj>> = mapOf(
        1 to lstBooks,
        2 to lstNewspapers,
        3 to lstDisks
    )

    fun showShortInfoObjs(type: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }

        if (allObjs[type]!!.isEmpty())
            println("\n   Список объектов пуст.")
        else {
            println("\n   Список объектов:")
            allObjs[type]!!.forEachIndexed { index, obj ->
                print("${index + 1}. ")
                obj.showShortInfo()
            }
        }
    }

    fun showLongInfoObj(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        allObjs[type]!![numbObj].showLongInfo()
    }

    fun getCountObj(type: Int): Int {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }

        return allObjs[type]!!.size
    }

    fun takeHome(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        allObjs[type]!![numbObj].takeHome()
    }

    fun takeRead(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        allObjs[type]!![numbObj].takeRead()
    }

    fun returnObj(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        allObjs[type]!![numbObj].returnObj()
    }
}
