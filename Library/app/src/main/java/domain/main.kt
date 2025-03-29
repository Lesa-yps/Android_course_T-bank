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
            // второе меню (выбор/покупка объекта)
            print("\nВыберите номер объекта (или -1 для покупки нового, 0 для возврата в главное меню): ")
            var userChooseObj = readlnOrNull()?.toIntOrNull()
            val countChooseObj = library.getCountObj(userChooseType)
            while (userChooseObj !in -1..countChooseObj) {
                print("\nОшибка! Пожалуйста, попробуйте снова: ")
                userChooseObj = readlnOrNull()?.toIntOrNull()
            }
            if (userChooseObj == 0) break
            if (userChooseObj == -1) {
                library.buyObj(userChooseType)
                continue
            }
            userChooseObj = userChooseObj!!.minus(1)

            // третье меню (выбор действия с объектом)
            println("\n   Меню:\n" +
                    "1. Взять домой\n" +
                    "2. Читать в читальном зале\n" +
                    "3. Показать детальную информацию\n" +
                    "4. Вернуть\n" +
                    "5. Оцифровать\n" +
                    "6. Вернуться в главное меню\n")
            print("Выберите пункт меню: ")

            do {
                val userAction = readlnOrNull()?.toIntOrNull()
                when (userAction) {
                    1 -> library.takeHome(userChooseType, userChooseObj)
                    2 -> library.takeRead(userChooseType, userChooseObj)
                    3 -> library.showLongInfoObj(userChooseType, userChooseObj)
                    4 -> library.returnObj(userChooseType, userChooseObj)
                    5 -> library.digitize(userChooseType, userChooseObj)
                    6 -> flagGoToMainMenu = true
                    else -> println("Неверный выбор. Пожалуйста, попробуйте снова.")
                }
            } while (userAction !in 1..6)

        } while (!flagGoToMainMenu)
    }
}


inline fun <reified T> List<Any>.filterByType(): List<T> {
    return this.filterIsInstance<T>()
}