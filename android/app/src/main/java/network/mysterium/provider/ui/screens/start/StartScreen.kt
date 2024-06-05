package network.mysterium.provider.ui.screens.start

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.buttons.PrimaryButton
import network.mysterium.provider.ui.components.buttons.PrimaryTextButton
import network.mysterium.provider.ui.components.content.ScreenContent
import network.mysterium.provider.ui.components.logo.HeaderLogo
import network.mysterium.provider.ui.components.logo.HeaderLogoStyle
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import org.koin.androidx.compose.getViewModel

@Composable
fun StartScreen(
    onNavigate: (NavigationDestination) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    ScreenContent(
        header = {
            Header(modifier = Modifier.height(screenHeight * 0.6f))
        },
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
        HeaderLogo(
            style = HeaderLogoStyle.Big,
            modifier = Modifier.padding(top = 24.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        HeaderText()
        Spacer(modifier = Modifier.weight(1f))
        HeaderDescription()
    }
}

@Composable
private fun Content(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: StartScreenViewModel = getViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth()
                .padding(Paddings.continueButton),
            text = stringResource(id = R.string.onboard),
            onClick = {
                viewModel.trackOnboardClick()
                onNavigate(NavigationDestination.Settings(true))
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryTextButton(
            modifier = Modifier.padding(Paddings.default),
            text = stringResource(id = R.string.terms_and_conditions),
            color = Colors.grey500,
            onClick = {
                onNavigate(NavigationDestination.TAC)
            }
        )
    }
}

@Composable
private fun HeaderText() {
    Text(
        text = stringResource(id = R.string.earn_while_you_sleep),
        style = TextStyles.splashHeader,
        color = Colors.blue700,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HeaderDescription() {
    Text(
        modifier = Modifier.padding(bottom = Paddings.logoDescription),
        text = stringResource(id = R.string.sell_unused_bandwidth),
        style = TextStyles.highDescriptions,
        color = Colors.grey500,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    MysteriumTheme(LocalContext.current) {
        StartScreen {}
    }
}
