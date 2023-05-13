package network.mysterium.provider.ui.components.buttons

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Colors.textPrimary,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    PrimaryButton(
        modifier = modifier
            .defaultMinSize(minWidth = 110.dp)
            .height(32.dp),
        text = text,
        enabled = enabled,
        contentPadding = Paddings.secondaryButton,
        color = color,
        onClick = onClick
    )
}