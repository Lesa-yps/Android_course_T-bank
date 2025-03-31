package domain

class Library {
    private val lstBooks: MutableList<LibraryObj> = mutableListOf(
        Book(1, true, "Маугли", 202, "Редьярд Киплинг"),
        Book(2, true, "Война и мир", 1225, "Лев Толстой"),
        Book(3, false, "Преступление и наказание", 671, "Фёдор Достоевский"),
        Book(4, true, "1984", 328, "Джордж Оруэлл"),
        Book(5, false, "Гарри Поттер и философский камень", 432, "Дж. К. Роулинг"),
        Book(6, true, "Мастер и Маргарита", 470, "Михаил Булгаков"),
        Book(7, false, "Таинственный остров", 650, "Жюль Верн")
    )

    private val lstNewspapers: MutableList<LibraryObj> = mutableListOf(
        Newspaper(1, false, "Деревенская жизнь", 794, 1),
        Newspaper(2, true, "Комсомольская правда", 1123, 7),
        Newspaper(3, true, "Известия", 900, 5),
        Newspaper(4, false, "Московский комсомолец", 1050, 3),
        Newspaper(5, true, "Аргументы и факты", 870, 2)
    )

    private val lstDisks: MutableList<LibraryObj> = mutableListOf(
        Disk(1, true, "Дэдпул и Росомаха", DiskType.DVD),
        Disk(2, false, "Ледниковый период", DiskType.CD),
        Disk(3, true, "Матрица", DiskType.DVD),
        Disk(4, false, "Интерстеллар", DiskType.DVD),
        Disk(5, true, "Аватар", DiskType.DVD),
        Disk(6, true, "Властелин колец", DiskType.DVD)
    )

    private val allObjs: Map<Int, MutableList<LibraryObj>> = mapOf(
        1 to lstBooks,
        2 to lstNewspapers,
        3 to lstDisks
    )

    private val manager = Manager()

    private val bookStore = BookStore()
    private val newspaperStore = NewspaperStore()
    private val diskStore = DiskStore()

    private val digitizationOffice: DigitizationOffice<LibraryDigitizable> = DigitizationOffice()

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

        (allObjs[type]!![numbObj] as? LibraryTakableHome)?.takeHome() ?: print("Этот тип объекта не может быть взят домой.")
    }

    fun takeRead(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        (allObjs[type]!![numbObj] as? LibraryReadableHere)?.readHere() ?: print("Этот тип объекта не может быть прочитан в зале.")
    }

    fun returnObj(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        allObjs[type]!![numbObj].returnObj()
    }

    fun buyObj(type: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }

        val newObj = when (type) {
            1 -> manager.buy(bookStore)
            2 -> manager.buy(newspaperStore)
            3 -> manager.buy(diskStore)
            else -> throw IllegalArgumentException("Неверный тип объекта")
        }

        allObjs[type]?.add(newObj)
        println("Добавлен новый объект: ${newObj.humanReadableType}")
        newObj.showLongInfo()
    }

    fun digitize(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Ошибка: несуществующий тип объекта." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Ошибка: объекта под таким номером не существует." }

        val obj = allObjs[type]!![numbObj] as? LibraryDigitizable
        if (obj != null) {
            val newObj = digitizationOffice.digitize(obj)
            allObjs[3]?.add(newObj)
            newObj.showLongInfo()
        }
        else
            println("Этот тип объекта не может быть оцифрован.")
    }

    fun getBooks() = lstBooks
    fun getNewspapers() = lstNewspapers
    fun getDisks() = lstDisks
}