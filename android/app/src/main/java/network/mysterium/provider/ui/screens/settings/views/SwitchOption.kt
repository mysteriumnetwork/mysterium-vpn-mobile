package network.mysterium.provider.ui.screens.settings.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.ui.components.content.RoundedBox
import network.mysterium.provider.ui.components.switcher.Switcher
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun SwitchOption(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    RoundedBox(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row {
                Text(
                    text = title,
                    style = TextStyles.body
                )
                Spacer(modifier = Modifier.weight(1f))
                Switcher(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
            content?.invoke(this)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SwitchOptionPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SwitchOption(title = "Option 1",
            checked = true,
            onCheckedChange = {}
        )
        SwitchOption(title = "Option 2",
            checked = false,
            onCheckedChange = {}
        )

        SwitchOption(title = "Option 3",
            checked = false,
            onCheckedChange = {}
        ) {
            Text(
                text = "Extra content"
            )
        }
    }
}