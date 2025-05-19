package room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: Int,
    val isAvailable: Boolean,
    val name: String,
    val pages: Int,
    val author: String,
    val addedDate: Long
)

@Entity(tableName = "newspapers")
data class NewspaperEntity(
    @PrimaryKey val id: Int,
    val isAvailable: Boolean,
    val name: String,
    val issueNumber: Int,
    val month: Int,
    val addedDate: Long
)

@Entity(tableName = "disks")
data class DiskEntity(
    @PrimaryKey val id: Int,
    val isAvailable: Boolean,
    val name: String,
    val type: String,
    val addedDate: Long
)