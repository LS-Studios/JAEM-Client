package de.stubbe.jaem_client.view.screens.uds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.utils.base64StringToByteArray
import de.stubbe.jaem_client.utils.toBitmap
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider


@Composable
fun UDSUserRow(
    udsUserDto: UDSUserDto
) {
    Row(
        Modifier
            .clickable(
                interactionSource = null,
                indication = ripple(
                    bounded = true
                )
            ) {

            }
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Tiny
            )
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Profile picture
        ProfilePicture(
            modifier = Modifier
                .size(Dimensions.Size.Medium),
            profilePicture = udsUserDto.profilePicture?.base64StringToByteArray()?.toBitmap()
        )

        // User name and description
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Chat title
            Text(
                text = udsUserDto.username,
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge)
            )

            // Description
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Tiny)
            ) {
                Text(
                    text = udsUserDto.description ?: "",
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleMedium,
                        color = JAEMThemeProvider.current.textSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}