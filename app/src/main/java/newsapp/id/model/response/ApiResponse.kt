package newsapp.id.model.response

import newsapp.id.model.Article

data class ApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>,
)