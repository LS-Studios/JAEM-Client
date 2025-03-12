package de.stubbe.jaem_client.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import de.stubbe.jaem_client.data.DEEP_LINK_URL
import de.stubbe.jaem_client.data.jaemEnterHorizontally
import de.stubbe.jaem_client.data.jaemEnterVerticallyFromBottom
import de.stubbe.jaem_client.data.jaemEnterVerticallyFromTop
import de.stubbe.jaem_client.data.jaemExitHorizontally
import de.stubbe.jaem_client.data.jaemExitVerticallyFromBottom
import de.stubbe.jaem_client.data.jaemExitVerticallyFromTop
import de.stubbe.jaem_client.data.jaemPopEnterHorizontally
import de.stubbe.jaem_client.data.jaemPopExitHorizontally
import de.stubbe.jaem_client.data.jeamAppearImmediately
import de.stubbe.jaem_client.data.jeamFadeIn
import de.stubbe.jaem_client.data.jeamFadeOut
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.utils.isNavRouteOfType
import de.stubbe.jaem_client.utils.sharedViewModel
import de.stubbe.jaem_client.view.screens.chat.ChatScreen
import de.stubbe.jaem_client.view.screens.chatinfo.ChatInfoScreen
import de.stubbe.jaem_client.view.screens.chatoverview.ChatOverviewScreen
import de.stubbe.jaem_client.view.screens.editprofile.EditProfileScreen
import de.stubbe.jaem_client.view.screens.initdevicescreen.InitDeviceScreen
import de.stubbe.jaem_client.view.screens.settings.SettingsScreen
import de.stubbe.jaem_client.view.screens.uds.UDSScreen
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.SharedChatViewModel

/**
 * Navigation zwischen den Bildschirmen
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier
) {
    val viewModel: NavigationViewModel = hiltViewModel()

    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.init(navController)
    }

    SharedTransitionLayout {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = NavRoute.ChatOverview,
            popExitTransition = { jaemPopExitHorizontally }
        ) {
            composable<NavRoute.InitDevice>(
                enterTransition = { jaemEnterVerticallyFromTop },
                exitTransition = { jaemExitVerticallyFromTop },
                popExitTransition = { jaemExitVerticallyFromTop }
            ) {
                InitDeviceScreen(viewModel)
            }

            composable<NavRoute.UDS>(
                enterTransition = { jaemEnterVerticallyFromBottom },
                exitTransition = { jaemExitVerticallyFromBottom },
                popExitTransition = { jaemExitVerticallyFromBottom }
            ) {
                UDSScreen(viewModel)
            }

            composable<NavRoute.Settings>(
                enterTransition = { jaemEnterVerticallyFromTop },
                exitTransition = { jaemExitVerticallyFromTop },
                popExitTransition = { jaemExitVerticallyFromTop }
            ) {
                SettingsScreen(viewModel)
            }

            composable<NavRoute.ChatOverview>(
                enterTransition = { jaemEnterHorizontally },
                exitTransition = {
                    if (this.targetState.isNavRouteOfType<NavRoute.Chat>()) {
                        jaemExitHorizontally
                    } else {
                        jeamFadeOut
                    }
                },
                popEnterTransition = {
                    if (this.targetState.isNavRouteOfType<NavRoute.Chat>()) {
                        jaemPopEnterHorizontally
                    } else {
                        jeamAppearImmediately
                    }
                },
                popExitTransition = { jaemPopExitHorizontally }
            ) {
                ChatOverviewScreen(viewModel)
            }

            navigation<NavRoute.Chat>(startDestination = NavRoute.ChatMessages("", -1, false)) {
                composable<NavRoute.ChatMessages>(
                    enterTransition = { jaemEnterHorizontally },
                    exitTransition = {
                        if (this.targetState.isNavRouteOfType<NavRoute.Profile>()) {
                            jeamFadeOut
                        } else {
                            jaemExitHorizontally
                        }
                    },
                    popEnterTransition = {
                        if (this.initialState.isNavRouteOfType<NavRoute.Profile>()) {
                            jeamAppearImmediately
                        } else {
                            jaemPopEnterHorizontally
                        }
                    },
                    popExitTransition = { jaemPopExitHorizontally }
                ) { navBackStackEntry ->
                    val sharedChatViewModel = navBackStackEntry.sharedViewModel<SharedChatViewModel>(navController)
                    ChatScreen(
                        navigationViewModel = viewModel,
                        sharedChatViewModel = sharedChatViewModel,
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
                composable<NavRoute.Profile>(
                    enterTransition = { jeamFadeIn },
                    exitTransition = { jaemExitVerticallyFromBottom },
                    popEnterTransition = { jaemPopEnterHorizontally },
                    popExitTransition = { jaemExitVerticallyFromBottom }
                ) { navBackStackEntry ->
                    val sharedChatViewModel = navBackStackEntry.sharedViewModel<SharedChatViewModel>(navController)
                    ChatInfoScreen(
                        navigationViewModel = viewModel,
                        sharedChatViewModel = sharedChatViewModel,
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                }
            }

            composable<NavRoute.EditProfile>(
                enterTransition = {
                    jaemEnterVerticallyFromBottom
                },
                exitTransition = {
                    jaemExitVerticallyFromBottom
                },
                popExitTransition = {
                    jaemExitVerticallyFromBottom
                },
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEP_LINK_URL/share/{shareCode}"
                    }
                )
            ) {
                EditProfileScreen(
                    navigationViewModel = viewModel
                )
            }
        }
    }
}