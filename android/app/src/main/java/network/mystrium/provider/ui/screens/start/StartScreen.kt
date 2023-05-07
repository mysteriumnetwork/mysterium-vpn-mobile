package network.mystrium.provider.ui.screens.start

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.buttons.PrimaryButton
import network.mystrium.provider.ui.components.buttons.PrimaryTextButton
import network.mystrium.provider.ui.components.content.ScreenContent
import network.mystrium.provider.ui.components.logo.HeaderLogo
import network.mystrium.provider.ui.components.logo.HeaderLogoStyle
import network.mystrium.provider.ui.navigation.NavigationDestination
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.MysteriumTheme
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.TextStyles

@Composable
fun StartScreen(
    onNavigate: (NavigationDestination) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    ScreenContent(
        header = {
            Header(modifier = Modifier.height(screenHeight * 0.6f))
        }
    ) {
        Content(onNavigate = onNavigate)
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderLogo(style = HeaderLogoStyle.Big)
        Spacer(modifier = Modifier.weight(1f))
        HeaderText()
        Spacer(modifier = Modifier.weight(1f))
        HeaderDescription()
    }
}

@Composable
private fun Content(
    onNavigate: (NavigationDestination) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Paddings.onboardButton),
            text = stringResource(id = R.string.onboard),
            onClick = {
                // to implement
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryTextButton(
            modifier = Modifier.padding(Paddings.default),
            text = stringResource(id = R.string.terms_and_conditions),
            onClick = {
                onNavigate(NavigationDestination.TAC)
            }
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
        StartScreen {}
    }
}
