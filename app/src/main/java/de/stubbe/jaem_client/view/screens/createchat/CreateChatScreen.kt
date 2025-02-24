package de.stubbe.jaem_client.view.screens.createchat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.utils.toBitmap
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMTextFieldColors
import de.stubbe.jaem_client.view.variables.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.CreateChatViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel

@Composable
fun CreateChatScreen(
    navigationViewModel: NavigationViewModel,
    viewModel: CreateChatViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val profilePicture by viewModel.profilePicture.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val profileDescription by viewModel.profileDescription.collectAsState()

    val advancedOptionsOpen by viewModel.advancedOptionsOpen.collectAsState()

    val asymmetricEncryption by viewModel.asymmetricEncryption.collectAsState()
    val symmetricEncryption by viewModel.symmetricEncryption.collectAsState()

    ScreenBase(
        topBar = {
            CreateChatTopBar(
                oClose = {
                    navigationViewModel.goBack()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(Dimensions.Padding.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.Medium)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.Small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.profile_picture),
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleSmall,
                        color = JAEMThemeProvider.current.textSecondary
                    ),
                )

                ProfilePicture(
                    modifier = Modifier
                        .size(Dimensions.Size.Huge),
                    profilePicture = profilePicture?.toBitmap()
                )
            }


            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = profileName,
                shape = Dimensions.Shape.Rounded.Small,
                onValueChange = {
                    viewModel.changeProfileName(it)
                },
                textStyle = JAEMTextStyle(
                    MaterialTheme.typography.titleMedium,
                    color = JAEMThemeProvider.current.textPrimary
                ).copy(
                    fontSize = Dimensions.FontSize.Medium
                ),
                label = {
                    Text(
                        text = stringResource(R.string.profile_name),
                        style = JAEMTextStyle(
                            MaterialTheme.typography.titleSmall,
                            color = JAEMThemeProvider.current.textSecondary
                        ),
                    )
                },
                colors = JAEMTextFieldColors(),
                singleLine = true,
                maxLines = 1
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = profileDescription,
                shape = Dimensions.Shape.Rounded.Small,
                onValueChange = {
                    viewModel.changeProfileDescription(it)
                },
                textStyle = JAEMTextStyle(
                    MaterialTheme.typography.titleMedium,
                    color = JAEMThemeProvider.current.textPrimary
                ).copy(
                    fontSize = Dimensions.FontSize.Medium
                ),
                label = {
                    Text(
                        text = stringResource(R.string.profile_description),
                        style = JAEMTextStyle(
                            MaterialTheme.typography.titleSmall,
                            color = JAEMThemeProvider.current.textSecondary
                        ),
                    )
                },
                colors = JAEMTextFieldColors(),
                maxLines = 5
            )
        }
    }
}