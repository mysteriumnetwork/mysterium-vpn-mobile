package network.mysterium.provider.ui.screens.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.buttons.PrimaryButton
import network.mysterium.provider.ui.components.buttons.PrimaryTextButton
import network.mysterium.provider.ui.components.logo.HeaderLogo
import network.mysterium.provider.ui.components.logo.HeaderLogoStyle
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Corners
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import org.koin.androidx.compose.getViewModel
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars

@Composable
fun StartScreen(
    onNavigate: (NavigationDestination) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val backgroundShape = RoundedCornerShape(
        topStart = Corners.card,
        topEnd = Corners.card
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Header(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Box(
            modifier = Modifier
                .height(screenHeight * 0.3f)
                .clip(backgroundShape)
                .background(
                    color = Colors.primaryBg,
                    shape = backgroundShape
                )
                .clipToBounds()
        ) {
            Content(onNavigate = onNavigate)
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        HeaderLogo(
            style = HeaderLogoStyle.Big,
            modifier = Modifier.weight(1f),
        )
        HeaderText(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        HeaderDescription(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun Content(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: StartScreenViewModel = getViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.weight(1.3f)) {
            PrimaryButton(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.default),
                text = stringResource(id = R.string.onboard),
                onClick = {
                    viewModel.trackOnboardClick()
                    onNavigate(NavigationDestination.Settings(true))
                }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            PrimaryTextButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = stringResource(id = R.string.terms_and_conditions),
                style = TextStyles.terms,
                color = Colors.grey500,
                onClick = {
                    onNavigate(NavigationDestination.TAC)
                }
            )
        }
    }
}

@Composable
private fun HeaderText(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.earn_while_you_sleep),
        style = TextStyles.splashHeader,
        color = Colors.blue700,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HeaderDescription(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxHeight()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = Paddings.small),
            text = stringResource(id = R.string.sell_unused_bandwidth),
            style = TextStyles.highDescriptions,
            color = Colors.grey500,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
)
@Composable
fun StartScreenPreview() {
    MysteriumTheme(LocalContext.current) {
        StartScreen() {}
    }
}
