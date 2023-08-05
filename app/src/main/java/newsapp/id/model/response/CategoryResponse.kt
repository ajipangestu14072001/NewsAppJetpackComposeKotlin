package newsapp.id.model.response

import newsapp.id.model.Source

data class CategoryResponse(
    val sources: List<Source>,
    val status: String
)