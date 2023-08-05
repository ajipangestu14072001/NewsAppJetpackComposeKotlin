package newsapp.id.helper

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.LoadState
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import newsapp.id.R
import newsapp.id.model.Article
import newsapp.id.ui.theme.primaryColor
import newsapp.id.ui.theme.thirdColor
import newsapp.id.util.Border
import newsapp.id.viewmodel.NewsViewModel


@Composable
fun ArticleItem(
    article: Article?,
    onClick: () -> Unit,
) {
    article?.let {
        Column{
            Box(
                modifier = Modifier
                    .aspectRatio(16 / 9f)
                    .padding(vertical = 10.dp)
                    .clickable(onClick = onClick)
            ) {

                ShimmerImage(
                    imageUrl = article.urlToImage,
                    placeholderResId = R.drawable.img,
                    border = Border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))

                )
            }

            Text(text = it.title, modifier = Modifier.fillMaxWidth(), maxLines = 4)
        }
    }
}

@Composable
fun ShimmerImage(
    imageUrl: String? = null,
    placeholderResId: Int,
    border: Border? = null,
    modifier: Modifier
) {
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        error = painterResource(id = placeholderResId)
    )
    val showShimmer = remember { mutableStateOf(false) }
    showShimmer.value = painter.state is AsyncImagePainter.State.Loading

    val imageModifier = if (showShimmer.value || painter.state is AsyncImagePainter.State.Error) {
        modifier
            .shimmerBackground(showShimmer = showShimmer.value)
            .fillMaxWidth()
            .border(
                width = border?.width ?: 0.dp,
                color = border?.color ?: Color.Transparent,
                shape = border?.shape ?: RoundedCornerShape(0.dp)
            )
            .clip(border?.shape ?: RoundedCornerShape(0.dp))
    } else {
        modifier
            .shimmerBackground(showShimmer = showShimmer.value)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(0.dp))
    }

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = imageModifier
    )
}

fun Modifier.shimmerBackground(
    targetValue: Float = 1000f,
    showShimmer: Boolean,
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = composed {

    val transition = rememberInfiniteTransition(label = "")
    lateinit var brush: Brush
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800), repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    brush = if (showShimmer) {
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
    return@composed this.then(background(brush, shape))
}

@Composable
fun Loading() {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {

                val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = Modifier
                        .size(170.dp)
                        .padding(top = 10.dp)
                )

            }
        }
    }
}


@Composable
fun ShowRefreshLoading(loadState: LoadState) {
    when (loadState) {
        is LoadState.Loading -> {
            Loading()
        }

        is LoadState.Error -> {
            val errorMessage = (loadState.error as? Exception)?.message ?: "Unknown error occurred"
            FailLoadApi(message = errorMessage)
        }

        else -> {}
    }
}

@Composable
fun ShowAppendLoading(loadState: LoadState, context: Context) {
    when (loadState) {
        is LoadState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            val errorMessage = (loadState.error as? Exception)?.message ?: "Unknown error occurred"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        else -> {

        }
    }
}

@Composable
fun FailLoadApi(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.notfoundanimation))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .size(270.dp)
        )

        Text(text = message, textAlign = TextAlign.Center, color = Color.Gray)
    }
}
@Composable
fun Chip(
    category: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else primaryColor,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) primaryColor else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 8.dp),
                text = category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else primaryColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    autoFocus: Boolean,
    viewModel: NewsViewModel,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp, top = 20.dp)
            .clip(CircleShape)
            .background(thirdColor)
            .height(54.dp)
    ) {
        var searchInput: String by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        LaunchedEffect(key1 = searchInput) {
            if (viewModel.searchParam.value.trim().isNotEmpty() &&
                viewModel.searchParam.value.trim().length != viewModel.previousSearch.value.length
            ) {
                delay(750)
                onSearch()
                viewModel.previousSearch.value = searchInput.trim()
            }
        }

        TextField(
            value = searchInput,
            onValueChange = { newValue ->
                searchInput = if (newValue.trim().isNotEmpty()) newValue else ""
                viewModel.searchParam.value = searchInput
            },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester = focusRequester),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search...",
                    color = Color.Gray
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                containerColor = Color.Transparent,
                disabledTextColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ), keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (viewModel.searchParam.value.trim().isNotEmpty()) {
                        focusManager.clearFocus()
                        viewModel.searchParam.value = searchInput
                        if (searchInput != viewModel.previousSearch.value) {
                            viewModel.previousSearch.value = searchInput
                            onSearch()
                        }
                    }
                }
            ),
            trailingIcon = {
                LaunchedEffect(Unit) {
                    if (autoFocus) {
                        focusRequester.requestFocus()
                    }
                }
                Row {
                    AnimatedVisibility(visible = searchInput.trim().isNotEmpty()) {
                        IconButton(onClick = {

                            focusManager.clearFocus()
                            searchInput = ""
                            viewModel.searchParam.value = viewModel.previousSearch.value
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        )
    }
}

