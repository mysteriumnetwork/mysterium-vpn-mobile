package network.mystrium.provider.ui.screens.settings.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.buttons.PrimaryButton
import network.mystrium.provider.ui.components.content.RoundedBox
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.TextStyles

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
            PrimaryButton(
                modifier = Modifier
                    .defaultMinSize(minWidth = 110.dp)
                    .height(32.dp),
                text = actionName,
                contentPadding = Paddings.secondaryButton,
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