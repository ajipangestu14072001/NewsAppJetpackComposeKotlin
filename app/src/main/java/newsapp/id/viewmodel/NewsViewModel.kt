package newsapp.id.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import newsapp.id.model.Article
import newsapp.id.model.response.CategoryResponse
import newsapp.id.repository.NewsRepository
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private var _news = mutableStateOf<Flow<PagingData<Article>>>(value = emptyFlow())
    val news: State<Flow<PagingData<Article>>> = _news

    private var _category = mutableStateOf<CategoryResponse?>(value = null)
    val category: State<CategoryResponse?> = _category

    private var _source = mutableStateOf<CategoryResponse?>(value = null)
    val source: State<CategoryResponse?> = _source

    private var selectedCategory: MutableState<String> = mutableStateOf(value = "general")
    var selectedSource: MutableState<String> = mutableStateOf(value = "")
    var searchParam = mutableStateOf(value = "Google")
    var previousSearch = mutableStateOf(value = "")


    init {
        getCategory()
        getNewsSources(category = selectedCategory.value)
    }

    fun getNews(): Flow<PagingData<Article>> =
        repository.getNews(q = searchParam.value, sources = selectedSource.value).also { _news.value = it }.cachedIn(viewModelScope)

    private fun getCategory() =
        viewModelScope.launch {
            try {
                val categoryResponse = repository.getCategory()
                _category.value = categoryResponse
            } catch (e: Exception) {
                _category.value = null
            }
        }

    private fun getNewsSources(category: String) =
        viewModelScope.launch {
            try {
                val categoryResponse = repository.getNewsSources(category= category)
                _source.value = categoryResponse
            } catch (e: Exception) {
                _source.value = null
            }
        }




    fun refreshNews(category: String = selectedSource.value) = getNewsSources(category = category)

}