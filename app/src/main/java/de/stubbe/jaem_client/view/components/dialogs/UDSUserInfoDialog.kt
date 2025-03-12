package de.stubbe.jaem_client.view.components.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.DialogActionModel
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.variables.Dimensions

@Composable
fun UDSUserInfoDialog(
    profile: ShareProfileModel,
    startChatWithProfile: (ShareProfileModel) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    DialogBase(
        onDismissRequest = onDismissRequest,
        title = profile.name,
        actions = listOf(
            DialogActionModel(
                text = stringResource(R.string.close),
                onClick = {
                    onDismissRequest()
                }
            ),
            DialogActionModel(
                text = stringResource(R.string.start_a_chat),
                onClick = {
                    startChatWithProfile(profile)
                    onDismissRequest()
                }
            )
        ),
        actionDivider = true
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(Dimensions.Size.Huge),
            profilePicture = profile.profilePicture
        )

        Spacer(modifier = Modifier.size(Dimensions.Spacing.Medium))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.Padding.Medium),
            text = profile.description,
            style = JAEMTextStyle(MaterialTheme.typography.titleLarge).copy(
                fontSize = Dimensions.FontSize.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}