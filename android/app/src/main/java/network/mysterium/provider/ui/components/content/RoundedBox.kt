package network.mysterium.provider.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Corners
import network.mysterium.provider.ui.theme.Paddings

@Composable
fun RoundedBox(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = Paddings.card,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(Corners.default)
            )
            .padding(contentPadding),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun RoundedBoxPreview() {
    RoundedBox(modifier = Modifier.padding(10.dp)) {
        Text(text = "Box")
    }
}
