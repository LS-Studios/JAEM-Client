package de.stubbe.jaem_client.view.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.utils.toBitmap
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

/**
 * TopBar für ein Profil.
 *
 * @param profilePicture Profilbild
 * @param profileName Profilname
 */
@Composable
fun ProfileTopBar(
    profile: ProfileModel?,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.Small),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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

        ProfilePicture(
            modifier = Modifier.size(Dimensions.Size.Huge),
            profilePicture = profile?.image?.toBitmap()
        )

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
    }
}