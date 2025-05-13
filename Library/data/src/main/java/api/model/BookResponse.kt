package api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VolumesResponse(
    @SerialName("items") val items: List<BookResponse>? = null,
    @SerialName("totalItems") val totalItems: Int
)

@Serializable
data class BookResponse(
    @SerialName("id") val id: String,
    @SerialName("volumeInfo") val volumeInfo: VolumeInfo
)

@Serializable
data class VolumeInfo(
    @SerialName("title") val title: String? = null,
    @SerialName("authors") val authors: List<String>? = null,
    @SerialName("pageCount") val pageCount: Int? = null,
    @SerialName("identifier") val identifier: List<String>? = null
)