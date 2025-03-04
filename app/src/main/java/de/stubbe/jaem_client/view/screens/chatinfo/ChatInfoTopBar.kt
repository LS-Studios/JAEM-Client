package de.stubbe.jaem_client.view.screens.chatinfo

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.PROFILE_PICTURE_TRANSITION
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoTopBar(
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    profilePicture: Bitmap?,
    onClose: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .height(Dimensions.Size.Huge)
            .padding(Dimensions.Padding.TopBar),
        title = {
            with(sharedTransitionScope) {
                ProfilePicture(
                    modifier = Modifier
                        .size(Dimensions.Size.Huge)
                        .sharedElement(
                            rememberSharedContentState(key = PROFILE_PICTURE_TRANSITION),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    profilePicture = profilePicture,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick =  {
                    onClose()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_bt),
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        },
        actions = {
            IconButton(
                onClick =  {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_actions_bt),
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        },
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = JAEMThemeProvider.current.background),
    )
}