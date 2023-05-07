package network.mystrium.provider.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import network.mystrium.provider.ui.screens.start.StartScreen
import network.mystrium.provider.ui.screens.tac.TermsAndConditionsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.Route.START
    ) {
        composable(route = Navigation.Route.START) {
            StartScreen(
                toOnboard = {

                },
                toTAC = {
                    navController.navigate(route = Navigation.Route.TAC)
                }
            )
        }
        composable(route = Navigation.Route.TAC) {
            TermsAndConditionsScreen(
                toBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

object Navigation {
    object Route {
        const val START = "start"
        const val TAC = "tac"
    }
}
