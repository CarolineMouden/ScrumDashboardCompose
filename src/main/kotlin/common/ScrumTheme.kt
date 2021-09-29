package common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val colorPrimary = Color(0xff303f9f)
val colorSecondary = Color(0xffcfd8dc)
val colorAction = Color(0xffA0B9BF)
val colorInput = Color(0xffA5CCD1)

@Composable
fun ScrumTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = lightColors(
            primary = colorPrimary,
            secondary = colorSecondary,
        ),
        content = content
    )
}

val padding_m = 8.dp
val iconSize = 25.dp