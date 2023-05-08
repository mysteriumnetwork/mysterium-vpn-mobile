package network.mystrium.provider.ui.components.switcher

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import network.mystrium.provider.R

@Composable
fun Switcher(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            onCheckedChange(checked.not())
        },
        painter = painterResource(
            id = if (checked) {
                R.drawable.ic_switcher_on
            } else {
                R.drawable.ic_switcher_off
            }
        ),
        contentDescription = null
    )
}

@Preview(showBackground = true)
@Composable
private fun SwitcherPreview() {
    Column {
        Switcher(
            checked = false,
            onCheckedChange = {}
        )
        Switcher(
            checked = true,
            onCheckedChange = {}
        )
    }
}