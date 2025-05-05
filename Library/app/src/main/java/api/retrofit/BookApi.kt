package api.retrofit

import api.model.BookResponse
import retrofit2.http.Query
import api.model.VolumesResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface BookApi {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String = "AIzaSyB-Pf3x8UvcYeWHLPY6SIeKow8Mwg6odO4",
        @Query("printType") printType: String = "books",
        @Query("projection") projection: String = "lite"
    ): VolumesResponse

    @GET("volumes/{bookId}")
    suspend fun getBookById(
        @Path("bookId") bookId: String,
        @Query("projection") projection: String = "lite"
    ): BookResponse
}