package network.mysterium.provider.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavController.Companion.KEY_DEEP_LINK_INTENT
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import network.mysterium.provider.Config
import network.mysterium.provider.DeeplinkPath
import network.mysterium.provider.ui.navigation.params.NodeUiParam
import network.mysterium.provider.ui.screens.home.HomeScreen
import network.mysterium.provider.ui.screens.launch.LaunchScreen
import network.mysterium.provider.ui.screens.nodeui.NodeUIScreen
import network.mysterium.provider.ui.screens.settings.SettingsScreen
import network.mysterium.provider.ui.screens.start.StartScreen
import network.mysterium.provider.ui.screens.tac.TACScreen
import network.mysterium.provider.utils.parcelable

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
        ) { backStackEntry ->
            val isOnboarding = backStackEntry.arguments?.getBoolean(Route.Arg.ONBOARDING) ?: false
            SettingsScreen(isOnboarding = isOnboarding) {
                navController.navigate(it)
            }
        }
        composable(
            route = Route.NODE_UI,
            deepLinks = listOf(
                navDeepLink { uriPattern = Config.deeplinkSSO.path() },
                navDeepLink { uriPattern = Config.deeplinkClaim.path() },
                navDeepLink { uriPattern = Config.deepLinkOnboardingClicking.path() },
            )
        ) { backStackEntry ->
            val host =
                backStackEntry.arguments?.parcelable<Intent>(KEY_DEEP_LINK_INTENT)?.data?.host
            val params = when (host) {
                DeeplinkPath.Scheme.SSO.scheme -> NodeUiParam(
                    scheme = DeeplinkPath.Scheme.SSO,
                    parameter = backStackEntry.arguments?.getString(Config.deeplinkSSO.parameterName)
                )

                DeeplinkPath.Scheme.CLAIM.scheme -> NodeUiParam(
                    scheme = DeeplinkPath.Scheme.CLAIM,
                    parameter = backStackEntry.arguments?.getString(Config.deeplinkClaim.parameterName)
                )

                DeeplinkPath.Scheme.CLICKBOARDING.scheme -> NodeUiParam(
                    scheme = DeeplinkPath.Scheme.CLICKBOARDING,
                    parameter = backStackEntry.arguments?.getString(Config.deepLinkOnboardingClicking.parameterName)
                )

                else -> null
            }
            NodeUIScreen(params) {
                navController.navigate(it)
            }
        }
        composable(route = Route.HOME) {
            HomeScreen {
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
    data class NodeUI(val isOnboarding: Boolean = false) : NavigationDestination()
    object Home : NavigationDestination()
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
    const val NODE_UI = "node_ui?${Arg.ONBOARDING}={${Arg.ONBOARDING}}"
    const val HOME = "home"
}

val NavigationDestination.route: String
    get() = when (this) {
        NavigationDestination.PopBack -> ""
        NavigationDestination.Launch -> Route.LAUNCH
        NavigationDestination.Start -> Route.START
        NavigationDestination.TAC -> Route.TAC
        is NavigationDestination.Settings -> Route.SETTINGS
        is NavigationDestination.NodeUI -> Route.NODE_UI
        NavigationDestination.Home -> Route.HOME
    }

fun NavController.navigate(destination: NavigationDestination) {
    when (destination) {
        NavigationDestination.PopBack -> {
            popBackStack()
        }

        NavigationDestination.Launch,
        NavigationDestination.Start,
        NavigationDestination.Home -> {
            navigate(destination.route, true)
        }

        is NavigationDestination.NodeUI -> {
            val route = "node_ui?${Route.Arg.ONBOARDING}=${destination.isOnboarding}"
            navigate(route, true)
        }

        is NavigationDestination.Settings -> {
            val route = "settings?${Route.Arg.ONBOARDING}=${destination.isOnboarding}"
            navigate(route, !destination.isOnboarding)
        }

        else -> navigate(
            destination.route,
        )
    }
}

private fun NavController.navigate(
    route: String,
    asRoot: Boolean = false
) {
    if (asRoot) {
        navigate(route) {
            backQueue.firstNotNullOfOrNull { it.destination.route }?.let {
                popUpTo(it) {
                    inclusive = true
                }
            }
        }
    } else {
        navigate(route)
    }
}
