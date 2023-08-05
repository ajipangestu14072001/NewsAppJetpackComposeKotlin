package newsapp.id.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import newsapp.id.network.ApiService
import newsapp.id.pagging.NewsSource
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getNews(q: String, sources: String) = Pager(
        config = PagingConfig(
            pageSize = 1,
        ),
        pagingSourceFactory = {
            NewsSource(api = apiService, q = q, sources = sources)
        }
    ).flow

    suspend fun getCategory() = apiService.getCategory()

    suspend fun getNewsSources(category: String) = apiService.getNewsSources(category = category)
}