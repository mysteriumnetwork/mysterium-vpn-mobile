package network.mysterium.provider.ui.screens.settings.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.ui.components.buttons.PrimaryButton
import network.mysterium.provider.ui.components.buttons.SecondaryButton
import network.mysterium.provider.ui.components.content.RoundedBox
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun ButtonOption(
    title: String,
    actionName: String,
    color: Color = Colors.primary,
    onClick: () -> Unit
) {
    RoundedBox(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = Paddings.cardButton
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = TextStyles.body
            )
            Spacer(modifier = Modifier.weight(1f))
            SecondaryButton(
                text = actionName,
                color = color,
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
private fun ButtonOptionPreview() {
    Column {
        ButtonOption(
            title = "Button 1",
            actionName = "Action 1"
        ) {

        }

        ButtonOption(
            title = "Button 2",
            actionName = "Action 2",
            color = Color.Black
        ) {

        }
    }
}