package network.mysterium.provider.ui.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Corners
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Colors.primaryBg,
    textStyle: TextStyle = TextStyles.button,
    enabled: Boolean = true,
    contentPadding: PaddingValues = Paddings.primaryButton,
    backgroundColor: Color = Colors.pink400,
    onPressedColor: Color = Colors.pink600,
    disabledColor: Color = Colors.grey100,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) onPressedColor else backgroundColor,
            disabledContainerColor = disabledColor,
        ),
        contentPadding = contentPadding,
        shape = RoundedCornerShape(Corners.small),
        onClick = onClick,
        interactionSource = interactionSource,
    ) {
        Text(
            text = text,
            style = textStyle,
            color = if (enabled) textColor else Colors.grey400,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    Column {
        PrimaryButton(text = "Enabled") {

        }
        PrimaryButton(
            text = "Disabled",
            enabled = false
        ) {

        }
    }
}
