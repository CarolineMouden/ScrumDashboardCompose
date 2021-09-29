package common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WelcomeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary
        ),
        onClick = onClick,
        modifier = modifier,
        content = content
    )
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorAction
        ),
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

@Composable
fun InputButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorInput
        ),
        onClick = onClick,
        modifier = modifier,
        content = content
    )
}