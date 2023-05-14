package network.mysterium.provider.ui.screens.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.R
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    message: String
) {
    Box(
        modifier = modifier
            .background(
                color = Colors.primary.copy(alpha = 0.1f),
                shape = CircleShape
            )
            .defaultMinSize(minHeight = 60.dp)
            .padding(Paddings.default),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.align(Alignment.CenterStart),
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Text(
            text = message,
            style = TextStyles.body,
            color = Colors.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorItemPreview() {
    Column(
        modifier = Modifier
            .width(500.dp)
            .padding(10.dp)
            .background(color = Color.White)
    ) {
        ErrorItem(
            modifier = Modifier.fillMaxWidth(),
            message = "Mobile data limit reached!"
        )
    }
}