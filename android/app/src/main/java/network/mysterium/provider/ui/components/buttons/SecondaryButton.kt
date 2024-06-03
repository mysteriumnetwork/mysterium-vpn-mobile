package network.mysterium.provider.ui.components.buttons

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Corners
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Colors.blue200,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = Colors.blue600,
            shape = RoundedCornerShape(Corners.small),
        )
    ) {
        PrimaryButton(
            modifier = Modifier
                .defaultMinSize(minWidth = 80.dp)
                .height(32.dp),
            text = text,
            textColor = Colors.blue600,
            textStyle = TextStyles.button2,
            enabled = enabled,
            contentPadding = Paddings.secondaryButton,
            color = color,
            onClick = onClick
        )
    }
}

@Preview
@Composable
fun SecondaryButtonPreview() {
    SecondaryButton(
        text = "Preview Text",
        onClick = {},
    )
}
