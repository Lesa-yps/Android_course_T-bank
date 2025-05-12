package domain

class Manager {
    fun <T : LibraryObj> buy(store: Store<T>): T {
        return store.sell()
    }
}