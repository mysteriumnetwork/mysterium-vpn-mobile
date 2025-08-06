package network.mysterium.provider.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import network.mysterium.node.model.NodeServiceType
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.buttons.PrimaryTextButton
import network.mysterium.provider.ui.components.buttons.SettingsButton
import network.mysterium.provider.ui.components.content.LogoScreenContent
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.screens.home.views.BalanceItem
import network.mysterium.provider.ui.screens.home.views.ErrorItem
import network.mysterium.provider.ui.screens.home.views.ServiceItem
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreenContent(
        state = state,
        onNavigate = onNavigate
    )
}

@Composable
fun HomeScreenContent(
    state: Home.State,
    onNavigate: (NavigationDestination) -> Unit
) {
    LogoScreenContent(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        navTrailing = {
            SettingsButton {
                onNavigate(NavigationDestination.Settings())
            }
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(Paddings.default),
                horizontalArrangement = Arrangement.spacedBy(Paddings.service),
                verticalArrangement = Arrangement.spacedBy(Paddings.service)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Header(text = stringResource(id = R.string.services))
                }

                items(state.services) {
                    ServiceItem(
                        modifier = Modifier.aspectRatio(1f),
                        service = it
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Header(
                            text = stringResource(id = R.string.unsettled_earnings)
                        )

                        BalanceItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Paddings.default),
                            value = state.balance
                        )

                        AnimatedVisibility(
                            modifier = Modifier.padding(top = Paddings.default),
                            visible = state.isLimitReached,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            ErrorItem(
                                modifier = Modifier.fillMaxWidth(),
                                message = stringResource(id = R.string.mobile_data_limit_reached)
                            )
                        }
                    }

                }
            }
            PrimaryTextButton(
                modifier = Modifier
                    .padding(Paddings.default),
                text = stringResource(id = R.string.terms_and_conditions),
                onClick = {
                    onNavigate(NavigationDestination.TAC)
                }
            )
        }
    }
}

@Composable
private fun Header(
    text: String
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Paddings.service),
        text = text,
        style = TextStyles.header,
        color = Colors.blue700,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenContentPreview() {
    HomeScreenContent(
        state = Home.State(
            services = listOf(
                NodeServiceType(NodeServiceType.Service.SCRAPING, NodeServiceType.State.STARTING),
                NodeServiceType(
                    NodeServiceType.Service.DATA_TRANSFER,
                    NodeServiceType.State.NOT_RUNNING
                ),
                NodeServiceType(NodeServiceType.Service.WIREGUARD, NodeServiceType.State.RUNNING),
            ),
            isLimitReached = true,
            balance = 0.0
        )
    ) {

    }
}
