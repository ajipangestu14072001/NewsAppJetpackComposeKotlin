package newsapp.id.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import newsapp.id.model.Article
import newsapp.id.network.ApiService
import newsapp.id.util.Constant.getErrorMessage
import retrofit2.HttpException


class NewsSource(
    private val api: ApiService,
    private val q: String,
    private val sources: String,
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.run {
                prevKey?.plus(1) ?: nextKey?.minus(1)
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = api.getNews(page = page, q = q, sources = sources)
            LoadResult.Page(
                data = response.articles,
                prevKey = (page - 1).takeIf { page > 1 },
                nextKey = (page + 1).takeIf { response.articles.isNotEmpty() }
            )
        } catch (e: HttpException) {
            val errorMessage = getErrorMessage(e.response()?.errorBody()?.string())
            LoadResult.Error(Exception(errorMessage))
        }
    }
}

