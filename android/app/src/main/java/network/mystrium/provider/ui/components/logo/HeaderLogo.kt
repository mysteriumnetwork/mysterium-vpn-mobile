package network.mystrium.provider.ui.components.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mystrium.provider.R
import network.mystrium.provider.ui.theme.Paddings

sealed class HeaderLogoStyle {
    object Small : HeaderLogoStyle()
    object Big : HeaderLogoStyle()
}

@Composable
fun HeaderLogo(style: HeaderLogoStyle = HeaderLogoStyle.Small) {
    val iconWidth = when (style) {
        HeaderLogoStyle.Big -> 57.dp
        HeaderLogoStyle.Small -> 36.dp
    }
    val nameHeight = when (style) {
        HeaderLogoStyle.Big -> 18.dp
        HeaderLogoStyle.Small -> 11 .dp
    }
    val spacing = when (style) {
        HeaderLogoStyle.Big -> Paddings.default
        HeaderLogoStyle.Small -> Paddings.small
    }
    Row(
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

@Preview(showBackground = true)
@Composable
private fun HeaderLogoPreview() {
    Column(modifier = Modifier.background(Color.Black)) {
        HeaderLogo(style = HeaderLogoStyle.Small)
        HeaderLogo(style = HeaderLogoStyle.Big)
    }
}
