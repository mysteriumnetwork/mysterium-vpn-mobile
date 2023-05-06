package network.mystrium.provider.ui.screens.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.buttons.PrimaryButton
import network.mystrium.provider.ui.components.buttons.PrimaryTextButton
import network.mystrium.provider.ui.components.content.ScreenContent
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.MysteriumTheme
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.TextStyles

@Composable
fun StartScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    ScreenContent(
        header = {
            Header(modifier = Modifier.height(screenHeight * 0.6f))
        }
    ) {
        Content()
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderLogo()
        Spacer(modifier = Modifier.weight(1f))
        HeaderText()
        Spacer(modifier = Modifier.weight(1f))
        HeaderDescription()
    }
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Paddings.onboardButton),
            text = stringResource(id = R.string.onboard)
        ) {
            // to implemnt
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryTextButton(text = stringResource(id = R.string.terms_and_conditions)) {
            // to implemnt
        }
    }
}

@Composable
private fun HeaderLogo() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Paddings.default, Alignment.CenterHorizontally)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null
        )
        Image(
            painter = painterResource(id = R.drawable.ic_logo_name),
            contentDescription = null
        )
    }
}

@Composable
private fun HeaderText() {
    val text1 = stringResource(id = R.string.earn)
    val text2 = stringResource(id = R.string.earn_while_sleep)
    Text(
        text = buildAnnotatedString {
            withStyle(style = TextStyles.logoParagraph) {
                withStyle(style = TextStyles.logoSpan.copy(color = Colors.primary)) {
                    append(text1)
                }
                withStyle(style = TextStyles.logoSpan.copy(color = Color.White)) {
                    append(text2)
                }
            }
        },
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HeaderDescription() {
    Text(
        modifier = Modifier.padding(bottom = Paddings.logoDescription),
        text = stringResource(id = R.string.sell_unused_bandwidth),
        style = TextStyles.body,
        color = Colors.textBg,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    MysteriumTheme {
        StartScreen()
    }
}
