package newsapp.id.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

data class Border(
    val width: Dp,
    val color: Color,
    val shape: Shape? = null
)