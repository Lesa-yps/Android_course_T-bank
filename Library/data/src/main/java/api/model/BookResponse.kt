package api.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@InternalSerializationApi @Serializable
data class VolumesResponse(
    @SerialName("items") val items: List<BookResponse>? = null,
    @SerialName("totalItems") val totalItems: Int
)

@InternalSerializationApi @Serializable
data class BookResponse(
    @SerialName("id") val id: String,
    @SerialName("volumeInfo") val volumeInfo: VolumeInfo
)

@InternalSerializationApi @Serializable
data class VolumeInfo(
    @SerialName("title") val title: String? = null,
    @SerialName("authors") val authors: List<String>? = null,
    @SerialName("pageCount") val pageCount: Int? = null,
    @SerialName("industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>? = null
)

@InternalSerializationApi @Serializable
data class IndustryIdentifier(
    @SerialName("type") val type: String,
    @SerialName("identifier") val identifier: String
)