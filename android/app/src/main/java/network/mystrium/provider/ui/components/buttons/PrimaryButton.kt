package network.mystrium.provider.ui.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import network.mystrium.provider.ui.theme.Corners
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.TextStyles

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        contentPadding = Paddings.buttonContent,
        shape = RoundedCornerShape(Corners.default),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = TextStyles.button
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
