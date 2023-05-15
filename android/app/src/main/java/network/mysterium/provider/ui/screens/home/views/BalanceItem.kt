package network.mysterium.provider.ui.screens.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.Config
import network.mysterium.provider.Formatters
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Composable
fun BalanceItem(
    modifier: Modifier = Modifier,
    value: Double
) {
    Box(
        modifier = modifier
            .background(
                color = Colors.balanceBg,
                shape = CircleShape
            )
            .defaultMinSize(minHeight = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(Paddings.default),
            text = Formatters.balance.format(0.0004),
            style = TextStyles.balance
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BalanceItemPreview() {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .background(color = Color.White)
    ) {
        BalanceItem(value = 4444.00)
        BalanceItem(value = 4.50)
    }
}