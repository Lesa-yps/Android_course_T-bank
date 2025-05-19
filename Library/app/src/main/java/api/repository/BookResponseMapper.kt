package api.repository

import api.model.BookResponse
import domain.Book
import kotlin.math.absoluteValue

internal object BookResponseMapper {
    fun map(response: BookResponse): Book {
        // генерируется уникальный ID (используется ISBN если есть, иначе ID от Google)
        val uniqueId = response.volumeInfo.industryIdentifiers
            ?.firstOrNull { it.type == "ISBN_13" || it.type == "ISBN_10" }
            ?.identifier
            ?.replace("-", "") // удаление дефисов в ISBN
            ?.takeIf { it.isNotBlank() }
            ?: response.id // fallback на Google ID

        return Book(
            id = uniqueId.hashCode().absoluteValue,
            isAvailable = true,
            name = response.volumeInfo.title ?: "Unknown Title",
            pages = response.volumeInfo.pageCount ?: 0,
            author = response.volumeInfo.authors?.joinToString(", ") ?: "Unknown Author",
            addedDate = System.currentTimeMillis()
        )
    }
}