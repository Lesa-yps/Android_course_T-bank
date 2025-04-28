package room

import domain.Book
import domain.Disk
import domain.DiskType
import domain.Newspaper


fun BookEntity.toDomain() = Book(id, isAvailable, name, pages, author)
fun Book.toEntity() = BookEntity(id, isAvailable, name, pages, author)

fun NewspaperEntity.toDomain() = Newspaper(id, isAvailable, name, issueNumber, month)
fun Newspaper.toEntity() = NewspaperEntity(id, isAvailable, name, issueNumber, month)

fun DiskEntity.toDomain() = Disk(id, isAvailable, name, DiskType.valueOf(type))
fun Disk.toEntity() = DiskEntity(id, isAvailable, name, type.name)
