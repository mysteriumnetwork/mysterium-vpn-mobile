package network.mysterium.provider.ui.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun PrimaryTextButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    color: Color = Colors.grey500,
    style: TextStyle = TextStyles.button,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = style,
            color = color.copy(if (enabled) 1f else 0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryTextButtonPreview() {
    Column {
        PrimaryTextButton(text = "Enabled") {
        }
        PrimaryTextButton(text = "Disabled", enabled = false) {
        }
    }
}
