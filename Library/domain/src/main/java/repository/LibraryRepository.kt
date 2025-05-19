package repository

import domain.LibraryObj
import utils.SortType

interface LibraryRepository {
    suspend fun getItemsSorted(
        sortType: SortType,
        offsetBooks: Int,
        offsetNewspapers: Int,
        offsetDisks: Int,
        limit: Int
    ): List<LibraryObj>

    suspend fun getItemCount(): Int

    suspend fun addItem(obj: LibraryObj)

    suspend fun removeItem(obj: LibraryObj)

    suspend fun searchGoogleBooks(query: String): List<LibraryObj>
}