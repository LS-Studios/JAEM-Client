package de.stubbe.jaem_client.view.screens.chatoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.utils.formatTime
import de.stubbe.jaem_client.utils.toLocalDateTime
import de.stubbe.jaem_client.view.components.ExtensionPresentation
import de.stubbe.jaem_client.view.components.LoadingIfNull
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.ChatOverviewViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Darstellung einer Chatzeile
 *
 * @param navigationViewModel Navigation view model
 * @param chatPresentationModel Chat presentation model
 */
@Composable
fun ChatRow(
    navigationViewModel: NavigationViewModel,
    chatOverviewViewModel: ChatOverviewViewModel,
    chatPresentationModel: ChatPresentationModel
) {
    val userProfileUid by chatOverviewViewModel.userProfileUid.collectAsState()

    Row(
        Modifier
            .clickable(
                interactionSource = null,
                indication = ripple(
                    bounded = true
                )
            ) {
                navigationViewModel.changeScreen(
                    NavRoute.ChatMessages(
                        chatPresentationModel.chat.chatPartnerUid,
                        chatPresentationModel.chat.id,
                        false
                    )
                )
            }
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Tiny
            )
            .height(IntrinsicSize.Max)
        ,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Profile picture
        ProfilePicture(
            modifier = Modifier
                .size(Dimensions.Size.Medium),
            profilePicture = chatPresentationModel.profilePicture
        )

        // Chat title and last message
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Chat title
            Text(
                text = chatPresentationModel.name,
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge)
            )

            // Last message
            if (chatPresentationModel.lastMessage != null) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Tiny)
                ) {
                    val lastMessage = chatPresentationModel.lastMessage

                    // Checkmark if message was delivered
                    if (lastMessage.deliveryTime != null && lastMessage.senderUid == userProfileUid) {
                        Icon(
                            modifier = Modifier
                                .size(Dimensions.Size.Tiny),
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = JAEMThemeProvider.current.textSecondary
                        )
                    }
                    if (lastMessage.attachments != null) {
                        var file: File? by remember { mutableStateOf(null) }

                        LaunchedEffect(Unit) {
                            launch(Dispatchers.IO) {
                                if (lastMessage.attachments.type == AttachmentType.FILE) {
                                    file = File(lastMessage.attachments.attachmentPaths.first())
                                }
                            }
                        }

                        LoadingIfNull(file) {
                            ExtensionPresentation(
                                extension = file!!.extension
                            )
                        }
                    }
                    Text(
                        text = lastMessage.stringContent  ?: "",
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
        // Extra information
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.End
        ) {
            // Send time of last message
            Text(
                text = chatPresentationModel.lastMessage?.sendTime?.toLocalDateTime()?.formatTime() ?: "",
                style = JAEMTextStyle(
                    MaterialTheme.typography.titleSmall,
                    color = if (chatPresentationModel.unreadMessages.isNotEmpty()) JAEMThemeProvider.current.accent else JAEMThemeProvider.current.textSecondary
                )
            )

            // Streak and unread messages
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak
                Text(
                    text = "\uD83D\uDD25 35",
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleSmall,
                        color = JAEMThemeProvider.current.textSecondary
                    )
                )

                // Unread messages count
                if (chatPresentationModel.unreadMessages.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(
                                JAEMThemeProvider.current.accent,
                                shape = CircleShape
                            )
                            .sizeIn(minWidth = Dimensions.Size.Tiny, maxHeight = Dimensions.Size.Tiny)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = chatPresentationModel.unreadMessages.size.toString(),
                            style = JAEMTextStyle(MaterialTheme.typography.titleSmall)
                        )
                    }
                }
            }
        }
    }
}