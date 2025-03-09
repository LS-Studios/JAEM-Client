package de.stubbe.jaem_client.view.screens.editprofile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.utils.toBitmap
import de.stubbe.jaem_client.utils.toByteArray
import de.stubbe.jaem_client.view.components.LoadingOverlay
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.components.dialogs.InfoDialog
import de.stubbe.jaem_client.view.components.filepicker.JAEMPickFileAndCrop
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.EditProfileViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navigationViewModel: NavigationViewModel,
) {
    val viewModel: EditProfileViewModel = hiltViewModel()

    val profilePicture by viewModel.profilePicture.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val profileDescription by viewModel.profileDescription.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val imagePickerIsOpen by viewModel.imagePickerIsOpen.collectAsState()

    val creationError by viewModel.creationError.collectAsState()
    val profileAlreadyExists by viewModel.profileAlreadyExists.collectAsState()

    val createProfile = viewModel.createProfile

    var loadImage by remember { mutableStateOf(false) }
    val fetchingProfile by viewModel.fetchingProfile.collectAsState()

    val context = LocalContext.current

    ScreenBase(
        topBar = {
            CreateChatTopBar(
                createProfile = createProfile,
                oClose = {
                    navigationViewModel.goBack()
                }
            )
        }
    ) {
        LoadingOverlay(loadImage || fetchingProfile) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .padding(Dimensions.Padding.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.Medium)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = null,
                                indication = ripple(
                                    bounded = true
                                )
                            ) {
                                if (!createProfile) {
                                    viewModel.openImagePicker()
                                }
                            },
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
                        enabled = !createProfile,
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
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .imePadding()
                        .padding(
                            start = Dimensions.Padding.Medium,
                            end = Dimensions.Padding.Medium,
                            bottom = Dimensions.Padding.Medium
                        )
                        .border(
                            width = Dimensions.Border.ThinBorder,
                            color = JAEMThemeProvider.current.border,
                            shape = Dimensions.Shape.Rounded.Small
                        ),
                    onClick = {
                        viewModel.updateProfile()
                        navigationViewModel.goBack()
                    },
                    containerColor = JAEMThemeProvider.current.primary
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                vertical = Dimensions.Padding.Small,
                                horizontal = Dimensions.Padding.Medium
                            ),
                        text = stringResource(R.string.save),
                        style = JAEMTextStyle(
                            MaterialTheme.typography.titleMedium,
                        )
                    )
                }
            }
        }
    }

    if (creationError) {
        InfoDialog(
            icon = Icons.Default.Error,
            title = stringResource(R.string.shared_code_error_title),
            message = stringResource(R.string.shared_code_error_message),
        ) {
            navigationViewModel.goBack()
        }
    }

    if (profileAlreadyExists) {
        InfoDialog(
            icon = Icons.Default.Info,
            title = stringResource(R.string.profile_already_exists_title),
            message = stringResource(R.string.profile_already_exists_message),
        ) {
            navigationViewModel.goBack()
        }
    }

    if (imagePickerIsOpen) {
        JAEMPickFileAndCrop(
            onDismiss = {
                viewModel.closeImagePicker()
            },
            selected = { uri ->
                coroutineScope.launch(Dispatchers.IO) {
                    loadImage = true

                    val image = uri.toBitmap(context)

                    if (image != null) {
                        viewModel.changeProfilePicture(image.toByteArray())
                    }

                    viewModel.closeImagePicker()
                    loadImage = false
                }
            }
        )
    }
}