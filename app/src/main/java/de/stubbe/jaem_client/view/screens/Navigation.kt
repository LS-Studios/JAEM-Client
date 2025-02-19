package de.stubbe.jaem_client.view.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.view.screens.chat.ChatScreen
import de.stubbe.jaem_client.view.screens.chatoverview.ChatOverviewScreen
import de.stubbe.jaem_client.view.screens.profile.ProfileScreen
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel

/**
 * Navigation zwischen den Bildschirmen
 *
 * @param innerPadding Innenabstand
 * @param viewModel Navigation view model
 */
@Composable
fun Navigation(
    modifier: Modifier,
    viewModel: NavigationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.init(navController)
    }

    Column {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = NavRoute.ChatOverview,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            composable<NavRoute.ChatOverview> {
                ChatOverviewScreen(viewModel)
            }
            composable<NavRoute.Chat> {
                val arguments = it.toRoute<NavRoute.Chat>()
                ChatScreen(viewModel, arguments)
            }
            composable<NavRoute.ProfileInfo> {
                val arguments = it.toRoute<NavRoute.ProfileInfo>()
                ProfileScreen(viewModel, arguments)
            }
        }
    }
}