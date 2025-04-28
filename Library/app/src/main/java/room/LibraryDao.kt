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

    // Newspapers
    @Query("SELECT * FROM newspapers")
    suspend fun getAllNewspapers(): List<NewspaperEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewspaper(newspaper: NewspaperEntity)

    @Delete
    suspend fun deleteNewspaper(newspaper: NewspaperEntity)

    @Query("DELETE FROM newspapers")
    suspend fun deleteAllNewspapers()

    // Disks
    @Query("SELECT * FROM disks")
    suspend fun getAllDisks(): List<DiskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisk(disk: DiskEntity)

    @Delete
    suspend fun deleteDisk(disk: DiskEntity)

    @Query("DELETE FROM disks")
    suspend fun deleteAllDisks()

    // All
    suspend fun deleteAllItems() {
        deleteAllBooks()
        deleteAllNewspapers()
        deleteAllDisks()
    }
}
