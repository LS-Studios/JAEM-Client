package de.stubbe.jaem_client.view.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.utils.isNavRouteOfType
import de.stubbe.jaem_client.view.screens.chat.ChatScreen
import de.stubbe.jaem_client.view.screens.chatoverview.ChatOverviewScreen
import de.stubbe.jaem_client.view.screens.createchat.CreateChatScreen
import de.stubbe.jaem_client.view.screens.profile.ProfileScreen
import de.stubbe.jaem_client.view.variables.jaemEnterHorizontally
import de.stubbe.jaem_client.view.variables.jaemEnterVertically
import de.stubbe.jaem_client.view.variables.jaemExitHorizontally
import de.stubbe.jaem_client.view.variables.jaemExitVertically
import de.stubbe.jaem_client.view.variables.jaemPopEnterHorizontally
import de.stubbe.jaem_client.view.variables.jaemPopExitHorizontally
import de.stubbe.jaem_client.view.variables.jeamAppearImmediately
import de.stubbe.jaem_client.view.variables.jeamFadeIn
import de.stubbe.jaem_client.view.variables.jeamFadeOut
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel

/**
 * Navigation zwischen den Bildschirmen
 *
 * @param innerPadding Innenabstand
 * @param viewModel Navigation view model
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier,
    viewModel: NavigationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
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
            composable<NavRoute.ChatOverview>(
                enterTransition = { jaemEnterHorizontally },
                exitTransition = {
                    if (this.targetState.isNavRouteOfType<NavRoute.CreateChat>()) {
                        jeamFadeOut
                    } else {
                        jaemExitHorizontally
                    }
                },
                popEnterTransition = {
                    if (this.initialState.isNavRouteOfType<NavRoute.CreateChat>()) {
                        jeamAppearImmediately
                    } else {
                        jaemPopEnterHorizontally
                    }
                },
                popExitTransition = { jaemPopExitHorizontally }
            ) {
                ChatOverviewScreen(viewModel)
            }
            composable<NavRoute.Chat>(
                enterTransition = { jaemEnterHorizontally },
                exitTransition = {
                    if (this.targetState.isNavRouteOfType<NavRoute.ProfileInfo>()) {
                        jeamFadeOut
                    } else {
                        jaemExitHorizontally
                    }
                },
                popEnterTransition = {
                    if (this.initialState.isNavRouteOfType<NavRoute.ProfileInfo>()) {
                        jeamAppearImmediately
                    } else {
                        jaemPopEnterHorizontally
                    }
                },
                popExitTransition = { jaemPopExitHorizontally }
            ) {
                val arguments = it.toRoute<NavRoute.Chat>()
                ChatScreen(
                    navigationViewModel = viewModel,
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    chatArguments = arguments
                )
            }
            composable<NavRoute.ProfileInfo>(
                enterTransition = { jeamFadeIn },
                exitTransition = { jaemExitVertically },
                popEnterTransition = { jaemPopEnterHorizontally },
                popExitTransition = { jaemExitVertically }
            ) {
                val arguments = it.toRoute<NavRoute.ProfileInfo>()
                ProfileScreen(
                    navigationViewModel = viewModel,
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    profileInfoArguments = arguments
                )
            }

            composable<NavRoute.CreateChat>(
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
                CreateChatScreen(
                    navigationViewModel = viewModel
                )
            }
        }
    }
}