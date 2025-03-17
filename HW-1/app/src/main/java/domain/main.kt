fun main() {
    println("Welcome, reader!")

    val library = Library()

    while (true) {

        // first menu
        println("\n   Main Menu:\n" +
                "1. Show books\n" +
                "2. Show newspapers\n" +
                "3. Show disks\n" +
                "4. Exit program\n")
        print("Select menu point: ")
        val userChooseType = readlnOrNull()?.toIntOrNull()
        if (userChooseType == 4) {
            println("Exiting program...")
            break
        }
        else if (userChooseType !in 1..3) {
            println("Invalid choice. Please try again.")
            continue
        }

        do {
            library.showShortInfoObjs(userChooseType!!)

            var flagGoToMainMenu = false

            // second menu
            print("Select an object number (or 0 for go to main menu): ")
            var userChooseObj = readlnOrNull()?.toIntOrNull()
            val countChooseObj = library.getCountObj(userChooseType)
            while (userChooseObj !in 0..countChooseObj) {
                print("\nError! Please try again: ")
                userChooseObj = readlnOrNull()?.toIntOrNull()
            }
            if (userChooseObj == 0)
                break
            userChooseObj = userChooseObj!!.minus(1)

            // third menu
            println(
                "\n   Menu:\n" +
                        "1. Borrow home\n" +
                        "2. Read in the reading hall\n" +
                        "3. Show detailed information\n" +
                        "4. Return\n" +
                        "5. Back to main menu\n"
            )
            print("Select menu point: ")

            do {
                val userAction = readlnOrNull()?.toIntOrNull()
                if (userAction == 1)
                    library.takeHome(userChooseType, userChooseObj)
                else if (userAction == 2)
                    library.takeRead(userChooseType, userChooseObj)
                else if (userAction == 3)
                    library.showLongInfoObj(userChooseType, userChooseObj)
                else if (userAction == 4)
                    library.returnObj(userChooseType, userChooseObj)
                else if (userAction == 5)
                    flagGoToMainMenu = true
                else
                    println("Invalid choice. Please try again.")
            } while (userAction !in 1..5)

        } while (!flagGoToMainMenu)
    }
}


open class LibraryObj(protected val id: Int, protected var isAvailable: Boolean, protected val name: String) {

    open fun showShortInfo() {
        val isAvailableYN = if (isAvailable) "Yes" else "No"
        println("$name available: $isAvailableYN")
    }
    open fun showLongInfo() {
        println("Error: No detailed information available.")
    }
    open fun takeHome() {
        println("Error: This object cannot be borrowed home.")
    }
    open fun takeRead() {
        println("Error: This object cannot be read in the reading hall.")
    }
    open fun returnObj() {
        println("Error: This object cannot be returned.")
    }
}


class Book(id: Int, isAvailable: Boolean, name: String, private val pages: Int, private val author: String) : LibraryObj(id, isAvailable, name) {

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Yes" else "No"
        println("Book: $name ($pages pages) by: $author with id: $id available: $isAvailableYN")
    }

    override fun takeHome() {
        if (!isAvailable)
            println("Error: This book is already borrowed.")
        else {
            isAvailable = false
            println("Book $id borrowed home.")
        }
    }

    override fun takeRead() {
        if (!isAvailable)
            println("Error: This book is already borrowed.")
        else {
            isAvailable = false
            println("Book $id taken to the reading hall.")
        }
    }

    override fun returnObj() {
        if (isAvailable)
            println("Error: This book is already in the library, it cannot be returned.")
        else {
            isAvailable = true
            println("Book $id returned.")
        }
    }
}


class Newspaper(id: Int, isAvailable: Boolean, name: String, private val issueNumber: Int) : LibraryObj(id, isAvailable, name) {

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Yes" else "No"
        println("Issue: $issueNumber of newspaper $name with id: $id available: $isAvailableYN")
    }

    override fun takeRead() {
        if (!isAvailable)
            println("Error: This newspaper is already taken.")
        else {
            isAvailable = false
            println("Newspaper $id taken to the reading hall.")
        }
    }

    override fun returnObj() {
        if (isAvailable)
            println("Error: This newspaper is already in the library, it cannot be returned.")
        else {
            isAvailable = true
            println("Newspaper $id returned.")
        }
    }
}


enum class DiskType { CD, DVD }

class Disk(id: Int, isAvailable: Boolean, name: String, private val type: DiskType) : LibraryObj(id, isAvailable, name) {

    override fun showLongInfo() {
        val isAvailableYN = if (isAvailable) "Yes" else "No"
        println("$type $name available: $isAvailableYN")
    }

    override fun takeHome() {
        if (!isAvailable)
            println("Error: This disk is already borrowed.")
        else {
            isAvailable = false
            println("Disk $id borrowed home.")
        }
    }

    override fun returnObj() {
        if (isAvailable)
            println("Error: This disk is already in the library, it cannot be returned.")
        else {
            isAvailable = true
            println("Disk $id returned.")
        }
    }
}


class Library () {
    private val lstBooks: List<LibraryObj> = listOf(
        Book(1, true, "Mowgli", 202, "Joseph Kipling"),
        Book(2, true, "War and Peace", 1225, "Leo Tolstoy")
    )
    private val lstNewspapers: List<LibraryObj> = listOf(
        Newspaper(3, false, "Rural Life", 794),
        Newspaper(4, true, "Komsomolskaya Pravda", 1123)
    )
    private val lstDisks: List<LibraryObj> = listOf(
        Disk(5, true, "Deadpool and Wolverine", DiskType.DVD),
        Disk(6, false, "Ice Age", DiskType.CD)
    )

    private val allObjs: Map<Int, List<LibraryObj>> = mapOf(
        1 to lstBooks,
        2 to lstNewspapers,
        3 to lstDisks
    )

    fun showShortInfoObjs(type: Int) {
        require(type in allObjs.keys) { "Error: Invalid object type." }

        if (allObjs[type]!!.isEmpty())
            println("\n   Object's list is empty.")
        else {
            println("\n   List of objects:")
            allObjs[type]!!.forEachIndexed { index, obj ->
                print("${index + 1}. ")
                obj.showShortInfo()
            }
        }
    }

    fun showLongInfoObj(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Error: Invalid object type." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Error: No such object number exists." }

        allObjs[type]!![numbObj].showLongInfo()
    }

    fun getCountObj(type: Int): Int {
        require(type in allObjs.keys) { "Error: Invalid object type." }

        return allObjs[type]!!.size
    }

    fun takeHome(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Error: Invalid object type." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Error: No such object number exists." }

        allObjs[type]!![numbObj].takeHome()
    }

    fun takeRead(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Error: Invalid object type." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Error: No such object number exists." }

        allObjs[type]!![numbObj].takeRead()
    }

    fun returnObj(type: Int, numbObj: Int) {
        require(type in allObjs.keys) { "Error: Invalid object type." }
        require(numbObj in 0..<allObjs[type]!!.size) { "Error: No such object number exists." }

        allObjs[type]!![numbObj].returnObj()
    }
}
