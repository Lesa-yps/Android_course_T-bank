package repository

import api.mapper.BookResponseMapper
import api.retrofit.BookApi
import domain.Book
import domain.Disk
import domain.LibraryObj
import domain.Newspaper
import room.LibraryDao
import room.toDomain
import room.toEntity
import utils.SortType
import utils.sortItemsComparator

class LibraryRepositoryImpl(
    private val dao: LibraryDao,
    private val api: BookApi
) : LibraryRepository {

    override suspend fun getItemsSorted(
        sortType: SortType,
        offsetBooks: Int,
        offsetNewspapers: Int,
        offsetDisks: Int,
        limit: Int
    ): List<LibraryObj> {
        val books = when (sortType) {
            SortType.BY_NAME -> dao.getBooksSortedByName(limit, offsetBooks)
            SortType.BY_DATE -> dao.getBooksSortedByDate(limit, offsetBooks)
        }.map { it.toDomain() }

        val newspapers = when (sortType) {
            SortType.BY_NAME -> dao.getNewspapersSortedByName(limit, offsetNewspapers)
            SortType.BY_DATE -> dao.getNewspapersSortedByDate(limit, offsetNewspapers)
        }.map { it.toDomain() }

        val disks = when (sortType) {
            SortType.BY_NAME -> dao.getDisksSortedByName(limit, offsetDisks)
            SortType.BY_DATE -> dao.getDisksSortedByDate(limit, offsetDisks)
        }.map { it.toDomain() }

        return (books + newspapers + disks).sortedWith(sortItemsComparator(sortType))
            .take(limit)
    }

    override suspend fun getItemCount(): Int {
        return dao.getCountAllItems()
    }

    override suspend fun addItem(obj: LibraryObj) {
        when (obj) {
            is Book -> dao.insertBook(obj.toEntity())
            is Newspaper -> dao.insertNewspaper(obj.toEntity())
            is Disk -> dao.insertDisk(obj.toEntity())
        }
    }

    override suspend fun removeItem(obj: LibraryObj) {
        when (obj) {
            is Book -> dao.deleteBook(obj.toEntity())
            is Newspaper -> dao.deleteNewspaper(obj.toEntity())
            is Disk -> dao.deleteDisk(obj.toEntity())
        }
    }

    override suspend fun searchGoogleBooks(query: String): List<LibraryObj> {
        val result = api.searchBooks(query)
        return result.items?.map { BookResponseMapper.map(it) } ?: emptyList()
    }
}
