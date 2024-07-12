package network.mysterium.provider.ui.components.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.R
import network.mysterium.provider.ui.theme.Paddings

sealed class HeaderLogoStyle {
    object Small : HeaderLogoStyle()
    object Big : HeaderLogoStyle()
}

@Composable
fun HeaderLogo(
    style: HeaderLogoStyle = HeaderLogoStyle.Small,
    modifier: Modifier = Modifier,
) {
    val iconWidth = when (style) {
        HeaderLogoStyle.Big -> 57.dp
        HeaderLogoStyle.Small -> 36.dp
    }
    val nameHeight = when (style) {
        HeaderLogoStyle.Big -> 22.dp
        HeaderLogoStyle.Small -> 14 .dp
    }
    val spacing = when (style) {
        HeaderLogoStyle.Big -> Paddings.default
        HeaderLogoStyle.Small -> Paddings.small
    }
    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
        ) {
            Image(
                modifier = Modifier.width(iconWidth),
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null
            )
            Image(
                modifier = Modifier.height(nameHeight),
                painter = painterResource(id = R.drawable.ic_logo_name),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderLogoPreview() {
    Column(modifier = Modifier.background(Color.Black)) {
        HeaderLogo(style = HeaderLogoStyle.Small)
        HeaderLogo(style = HeaderLogoStyle.Big)
    }
}
