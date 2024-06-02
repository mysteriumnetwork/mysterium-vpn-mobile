package network.mysterium.provider.ui.screens.settings.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.content.RoundedBox
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun ButtonOption(
    title: String,
    subTitle: String,
    painterResource: Painter? = null,
    onClick: () -> Unit
) {
    RoundedBox(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        contentPadding = Paddings.cardButton,
        color = Colors.cardBg,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = TextStyles.body3
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = subTitle,
                style = TextStyles.button2,
                color = Colors.primary,
            )
            painterResource?.let { painter ->
                Image(
                    modifier = Modifier.padding(start = 4.dp),
                    painter = painter,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ButtonOptionPreview() {
    Column {
        ButtonOption(
            title = "Button 1",
            subTitle = "Action 1"
        ) {

        }

        ButtonOption(
            title = "Button 2",
            subTitle = "Action 2",
            painterResource = painterResource(id = R.drawable.ic_shut_down)
        ) {

        }
    }
}
