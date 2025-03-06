package de.stubbe.jaem_client.view.screens

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
import de.stubbe.jaem_client.data.jaemEnterHorizontally
import de.stubbe.jaem_client.data.jaemEnterVertically
import de.stubbe.jaem_client.data.jaemExitHorizontally
import de.stubbe.jaem_client.data.jaemExitVertically
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
import de.stubbe.jaem_client.view.screens.deviceclientsetup.DeviceClientSetupScreen
import de.stubbe.jaem_client.view.screens.editprofile.EditProfileScreen
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.SharedChatViewModel

/**
 * Navigation zwischen den Bildschirmen
 */
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
            composable<NavRoute.DeviceClientSetup> {
                DeviceClientSetupScreen()
            }
            composable<NavRoute.ChatOverview>(
                enterTransition = { jaemEnterHorizontally },
                exitTransition = {
                    if (this.targetState.isNavRouteOfType<NavRoute.EditProfile>()) {
                        jeamFadeOut
                    } else {
                        jaemExitHorizontally
                    }
                },
                popEnterTransition = {
                    if (this.initialState.isNavRouteOfType<NavRoute.EditProfile>()) {
                        jeamAppearImmediately
                    } else {
                        jaemPopEnterHorizontally
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
                    exitTransition = { jaemExitVertically },
                    popEnterTransition = { jaemPopEnterHorizontally },
                    popExitTransition = { jaemExitVertically }
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
                    jaemEnterVertically
                },
                exitTransition = {
                    jaemExitVertically
                },
                popExitTransition = {
                    jaemExitVertically
                }
            ) {
                EditProfileScreen(
                    navigationViewModel = viewModel
                )
            }
        }
    }
}