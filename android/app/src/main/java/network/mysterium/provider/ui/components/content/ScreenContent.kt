package network.mysterium.provider.ui.components.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.logo.HeaderLogo
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Corners
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.Styles
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun LogoScreenContent(
    modifier: Modifier = Modifier,
    color: Color = Colors.primaryBg,
    header: (@Composable BoxScope.() -> Unit)? = null,
    navLeading: (@Composable RowScope.() -> Unit)? = null,
    navTrailing: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    ScreenContent(
        modifier = modifier,
        title = {
            HeaderLogo()
        },
        header = header,
        color = color,
        navLeading = navLeading,
        navTrailing = navTrailing,
        content = content
    )
}

@Composable
fun TitledScreenContent(
    modifier: Modifier = Modifier,
    title: String,
    color: Color = Colors.primaryBg,
    header: (@Composable BoxScope.() -> Unit)? = null,
    navLeading: (@Composable RowScope.() -> Unit)? = null,
    navTrailing: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    ScreenContent(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = TextStyles.navigationHeader,
                color = Colors.navHeader,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        color = color,
        header = header,
        navLeading = navLeading,
        navTrailing = navTrailing,
        content = content
    )
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    title: (@Composable BoxScope.() -> Unit)? = null,
    header: (@Composable BoxScope.() -> Unit)? = null,
    navLeading: (@Composable RowScope.() -> Unit)? = null,
    navTrailing: (@Composable RowScope.() -> Unit)? = null,
    color: Color = Colors.primaryBg,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .background(Styles.background)
            .fillMaxSize(),
    ) {
        if (title != null || navLeading != null || navTrailing != null) {
            Box(
                modifier = Modifier
                    .padding(Paddings.default)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    navLeading?.invoke(this)
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (title != null) {
                        title()
                    }
                }

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    navTrailing?.invoke(this)
                }
            }
        }
        if (header != null) {
            Box(
                modifier = Modifier
                    .padding(Paddings.default),
                content = header
            )
        }

        val backgroundShape = RoundedCornerShape(
            topStart = Corners.card,
            topEnd = Corners.card
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(backgroundShape)
                .background(
                    color = color,
                    shape = backgroundShape
                )
                .clipToBounds()
                .then(modifier)
        ) {
            content()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScreenContentPreview() {
    TitledScreenContent(
        modifier = Modifier,
        title = "Title",
        header = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Header",
                textAlign = TextAlign.Center
            )
        },
        navLeading = {
            IconButton(onClick = { /*TODO*/ }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null
                )
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
