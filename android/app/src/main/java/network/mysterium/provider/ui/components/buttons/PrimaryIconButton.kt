package network.mysterium.provider.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import network.mysterium.provider.R
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun PrimaryIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryTextButtonPreview() {
    PrimaryIconButton(iconResId = R.drawable.ic_back) {
    }
}
