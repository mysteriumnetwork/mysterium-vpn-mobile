package network.mystrium.provider.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import network.mystrium.provider.ui.screens.launch.LaunchScreen
import network.mystrium.provider.ui.screens.start.StartScreen
import network.mystrium.provider.ui.screens.tac.TACScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Launch.route
    ) {
        composable(route = NavigationDestination.Launch.route) {
            LaunchScreen {
                navController.navigate(it)
            }
        }
        composable(route = NavigationDestination.Start.route) {
            StartScreen {
                navController.navigate(it)
            }
        }
        composable(route = NavigationDestination.TAC.route) {
            TACScreen {
                navController.navigate(it)
            }
        }
    }
}

sealed class NavigationDestination {
    object PopBack : NavigationDestination()
    object Launch : NavigationDestination()
    object Start : NavigationDestination()
    object TAC : NavigationDestination()
}

private object Route {
    const val LAUNCH = "launch"
    const val START = "start"
    const val TAC = "tac"
}

val NavigationDestination.route: String
    get() = when (this) {
        NavigationDestination.PopBack -> ""
        NavigationDestination.Launch -> Route.LAUNCH
        NavigationDestination.Start -> Route.START
        NavigationDestination.TAC -> Route.TAC
    }

fun NavController.navigate(destination: NavigationDestination) {
    val controller = this
    when (destination) {
        NavigationDestination.PopBack -> popBackStack()
        NavigationDestination.Launch,
        NavigationDestination.Start -> {
            navigate(destination.route) {
                popUpToTop(controller)
            }
        }

        else -> navigate(destination.route)
    }
}

private fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive = true
    }
}
