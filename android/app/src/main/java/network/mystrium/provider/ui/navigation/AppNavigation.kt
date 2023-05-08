package network.mystrium.provider.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import network.mystrium.provider.ui.screens.launch.LaunchScreen
import network.mystrium.provider.ui.screens.settings.SettingsScreen
import network.mystrium.provider.ui.screens.start.StartScreen
import network.mystrium.provider.ui.screens.tac.TACScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.LAUNCH
    ) {
        composable(route = Route.LAUNCH) {
            LaunchScreen {
                navController.navigate(it)
            }
        }
        composable(route = Route.START) {
            StartScreen {
                navController.navigate(it)
            }
        }
        composable(route = Route.TAC) {
            TACScreen {
                navController.navigate(it)
            }
        }
        composable(
            route = Route.SETTINGS,
            arguments = listOf(
                navArgument(Route.Arg.ONBOARDING) {
                    type = NavType.BoolType
                }
            )
        ) {
            val isOnboarding = it.arguments?.getBoolean(Route.Arg.ONBOARDING) ?: false
            SettingsScreen(isOnboarding = isOnboarding) {
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
    data class Settings(val isOnboarding: Boolean = false) : NavigationDestination()
}

private object Route {
    object Arg {
        const val ONBOARDING = "onboarding"
    }

    const val LAUNCH = "launch"
    const val START = "start"
    const val TAC = "tac"
    const val SETTINGS = "settings?${Arg.ONBOARDING}={${Arg.ONBOARDING}}"
}

val NavigationDestination.route: String
    get() = when (this) {
        NavigationDestination.PopBack -> ""
        NavigationDestination.Launch -> Route.LAUNCH
        NavigationDestination.Start -> Route.START
        NavigationDestination.TAC -> Route.TAC
        is NavigationDestination.Settings -> Route.SETTINGS
    }

fun NavController.navigate(destination: NavigationDestination) {
    val controller = this
    when (destination) {
        NavigationDestination.PopBack -> {
            popBackStack()
        }

        NavigationDestination.Launch,
        NavigationDestination.Start -> {
            navigate(destination.route) {
                popUpToTop(controller)
            }
        }

        is NavigationDestination.Settings -> {
            navigate("settings?${Route.Arg.ONBOARDING}=${destination.isOnboarding}")
        }

        else -> navigate(
            destination.route,
        )
    }
}

private fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive = true
    }
}
