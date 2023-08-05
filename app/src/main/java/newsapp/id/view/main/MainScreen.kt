package newsapp.id.view.main

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import newsapp.id.R
import newsapp.id.helper.ArticleItem
import newsapp.id.helper.Chip
import newsapp.id.helper.FailLoadApi
import newsapp.id.helper.Loading
import newsapp.id.helper.SearchBar
import newsapp.id.helper.ShimmerImage
import newsapp.id.helper.ShowAppendLoading
import newsapp.id.helper.ShowRefreshLoading
import newsapp.id.navigation.Screen
import newsapp.id.util.Constant
import newsapp.id.util.Constant.isValidUrl
import newsapp.id.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val news = viewModel.news.value.collectAsLazyPagingItems()
    val category = viewModel.category.value?.sources
    val source = viewModel.source.value?.sources
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    val context = LocalContext.current
    val listState = rememberLazyListState()
    if (category.isNullOrEmpty()) Loading()
    CompositionLocalProvider(LocalRippleTheme provides Constant.NoRippleTheme) {

        Scaffold(
            content = {
                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
                    sheetState = state,
                    sheetContent = {
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 20.dp, bottom = 10.dp)
                        ) {
                            if (source != null) {
                                items(source.chunked(size = 4)) { items ->
                                    Row(
                                        modifier = Modifier.padding(
                                            start = 4.dp,
                                            end = 4.dp,
                                            top = 4.dp,
                                            bottom = 4.dp
                                        )
                                    ) {
                                        for ((index, item) in items.withIndex()) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .fillMaxWidth(1f / (4 - index))
                                                    .clickable(onClick = {
                                                        viewModel.selectedSource.value = item.id
                                                        viewModel.searchParam.value = item.name
                                                        viewModel.getNews()
                                                        scope.launch {
                                                            state.hide()
                                                        }
                                                    })
                                            ) {
                                                ShimmerImage(
                                                    imageUrl = "https://cdn-icons-png.flaticon.com/512/278/278019.png",
                                                    placeholderResId = R.drawable.img,
                                                    modifier = Modifier.size(50.dp)
                                                )
                                                Text(
                                                    text = item.name,
                                                    fontSize = 11.sp,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier
                                                        .wrapContentSize()
                                                        .padding(top = 5.dp),
                                                    lineHeight = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) {
                    category?.let { categories ->
                        if (categories.isNotEmpty()) {
                            val uniqueNewsSources = categories.distinctBy { it.category }
                            val selectedOptionState =
                                remember { mutableStateOf(categories.first()) }

                            SwipeRefresh(
                                state = swipeRefreshState,
                                onRefresh = {
                                    viewModel.getNews()
                                    viewModel.searchParam.value = viewModel.previousSearch.value
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues = it)
                                ) {

                                    SearchBar(
                                        viewModel = viewModel,
                                        autoFocus = false,
                                        onSearch = {
                                            viewModel.getNews()
                                        }
                                    )

                                    Text(
                                        text = "News Category",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(start = 16.dp, bottom = 2.dp, top = 6.dp)
                                    )

                                    LazyRow(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                    ) {
                                        items(uniqueNewsSources) { option ->
                                            var selected = selectedOptionState.value
                                            Chip(
                                                category = option.category,
                                                selected = option == selected,
                                                onClick = {
                                                    selected = option
                                                    viewModel.selectedSource.value = selected.category
                                                    viewModel.refreshNews(selected.category)
                                                    scope.launch { state.show() }
                                                }
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Article",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(start = 16.dp, bottom = 2.dp, top = 6.dp)
                                    )

                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                        ),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        items(news.itemCount) {
                                            val article = news[it]
                                            ArticleItem(article = article) {
                                                val articleUrl = article?.url
                                                val detail = articleUrl?.takeIf(::isValidUrl)?.let { url ->
                                                    "${Screen.WebView.route}/${Uri.encode(url)}"
                                                }

                                                detail?.also { navDetail ->
                                                    navController.navigate(navDetail) {
                                                        popUpTo(Screen.WebView.route) {
                                                            inclusive = true
                                                        }
                                                    }
                                                } ?: run {
                                                    Toast.makeText(context, "Something When Wrong", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }

                                        item {
                                            ShowRefreshLoading(
                                                loadState = news.loadState.refresh
                                            )
                                        }

                                        item {
                                            ShowAppendLoading(
                                                loadState = news.loadState.append,
                                                context = context
                                            )
                                        }
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(context, "Something When Wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        )
    }
}



