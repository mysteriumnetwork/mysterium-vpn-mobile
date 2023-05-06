package network.mystrium.provider.ui.components.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.Corners
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.Styles
import network.mystrium.provider.ui.theme.TextStyles

@Composable
fun ScreenContent(
    title: String? = null,
    header: (@Composable BoxScope.() -> Unit)? = null,
    navLeading: (@Composable RowScope.() -> Unit)? = null,
    navTrailing: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .background(Styles.background)
            .fillMaxSize(),
    ) {
        if (title != null) {
            Row(
                modifier = Modifier
                    .padding(Paddings.default)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Paddings.default,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navLeading?.invoke(this)
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    style = TextStyles.navigationHeader,
                    color = Colors.navHeader,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                navTrailing?.invoke(this)
            }
        }
        Box(modifier = Modifier
            .padding(Paddings.default)) {
            header?.invoke(this)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White, RoundedCornerShape(
                        topStart = Corners.card,
                        topEnd = Corners.card
                    )
                )
                .padding(Paddings.default)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenContentPreview() {
    ScreenContent(
        title = "Title Title Title Title Title Title Title",
        header = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Header",
                textAlign = TextAlign.Center
            )
        },
        navLeading = {
            Button(onClick = {}) {
                Text(text = "Action")
            }
        },
        navTrailing = {
            Button(onClick = {}) {
                Text(text = "Action")
            }
        }
    ) {
        Text(
            modifier = Modifier.height(600.dp),
            text = "Content"
        )
    }
}
