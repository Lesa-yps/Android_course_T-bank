package api.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Converter
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient


object RetrofitHelper {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    private fun createConverterFactory(): Converter.Factory {
        val contentType = "application/json"
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return json.asConverterFactory(contentType.toMediaType())
    }

    fun createRetrofit(): BookApi {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(createConverterFactory())
            .client(okHttpClient)
            .build()
        return retrofit.create(BookApi::class.java)
    }
}