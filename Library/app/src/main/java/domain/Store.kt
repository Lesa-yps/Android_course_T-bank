package domain

interface Store<out T : LibraryObj> {
    fun sell(): T
}