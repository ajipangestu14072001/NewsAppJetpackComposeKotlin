package newsapp.id.network

import newsapp.id.model.response.ApiResponse
import newsapp.id.model.response.CategoryResponse
import newsapp.id.util.Constant
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("q") q: String,
        @Query("language") language: String = "en",
        @Query("sources") sources: String,
        @Query("apiKey") apiKey: String = Constant.API_KEY
    ): ApiResponse

    @GET("top-headlines/sources")
    suspend fun getCategory(
        @Query("apiKey") apiKey: String = Constant.API_KEY
    ): CategoryResponse

    @GET("top-headlines/sources")
    suspend fun getNewsSources(
        @Query("category") category: String = "",
        @Query("language") language: String = "en",
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = Constant.API_KEY
    ): CategoryResponse

}