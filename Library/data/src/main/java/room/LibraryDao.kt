package room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LibraryDao {
    // Books
    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query("SELECT * FROM books ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getBooksSortedByName(limit: Int, offset: Int): List<BookEntity>

    @Query("SELECT * FROM books ORDER BY addedDate ASC LIMIT :limit OFFSET :offset")
    suspend fun getBooksSortedByDate(limit: Int, offset: Int): List<BookEntity>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int


    // Newspapers
    @Query("SELECT * FROM newspapers")
    suspend fun getAllNewspapers(): List<NewspaperEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewspaper(newspaper: NewspaperEntity)

    @Delete
    suspend fun deleteNewspaper(newspaper: NewspaperEntity)

    @Query("DELETE FROM newspapers")
    suspend fun deleteAllNewspapers()

    @Query("SELECT * FROM newspapers ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getNewspapersSortedByName(limit: Int, offset: Int): List<NewspaperEntity>

    @Query("SELECT * FROM newspapers ORDER BY addedDate ASC LIMIT :limit OFFSET :offset")
    suspend fun getNewspapersSortedByDate(limit: Int, offset: Int): List<NewspaperEntity>

    @Query("SELECT COUNT(*) FROM newspapers")
    suspend fun getNewspaperCount(): Int


    // Disks
    @Query("SELECT * FROM disks")
    suspend fun getAllDisks(): List<DiskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisk(disk: DiskEntity)

    @Delete
    suspend fun deleteDisk(disk: DiskEntity)

    @Query("DELETE FROM disks")
    suspend fun deleteAllDisks()

    @Query("SELECT * FROM disks ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getDisksSortedByName(limit: Int, offset: Int): List<DiskEntity>

    @Query("SELECT * FROM disks ORDER BY addedDate ASC LIMIT :limit OFFSET :offset")
    suspend fun getDisksSortedByDate(limit: Int, offset: Int): List<DiskEntity>

    @Query("SELECT COUNT(*) FROM disks")
    suspend fun getDiskCount(): Int


    // All
    suspend fun deleteAllItems() {
        deleteAllBooks()
        deleteAllNewspapers()
        deleteAllDisks()
    }

    suspend fun getCountAllItems(): Int {
        return getBookCount() + getNewspaperCount() + getDiskCount()
    }
}
