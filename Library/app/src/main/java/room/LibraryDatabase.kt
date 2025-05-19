package room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database abstract class for creating the data base.
 */
@Database(
    entities = [BookEntity::class, NewspaperEntity::class, DiskEntity::class],
    version = 2
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
}