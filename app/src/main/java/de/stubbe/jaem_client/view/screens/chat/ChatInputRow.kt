package de.stubbe.jaem_client.view.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.view.variables.JaemTextStyle
import java.io.File

/**
 * Chat Eingabezeile für Nachrichten und Anhänge
 *
 * @param modifier Modifier
 * @param messageString Nachricht
 * @param onMessageChange Änderung der Nachricht
 * @param attachment Anhänge
 * @param onClickAttache Klick auf Anhänge
 * @param onClickCamera Klick auf Kamera
 * @param onSendMessage Senden der Nachricht
 */
@Composable
fun ChatInputRow(
    modifier: Modifier = Modifier,
    messageString: String,
    onMessageChange: (String) -> Unit,
    attachment: File?,
    onClickAttache: () -> Unit,
    onClickCamera: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var isMultiline by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.Padding.Medium,
                end = Dimensions.Padding.Medium,
                bottom = Dimensions.Padding.Medium
            ),
        verticalAlignment = Alignment.Bottom
    ) {
        // Eingabefeld
        BasicTextField(
            modifier = Modifier
                .background(
                    color = JAEMThemeProvider.current.primary,
                    shape = Dimensions.Shape.Rounded.Small
                )
                .padding(
                    start = Dimensions.Padding.Medium,
                    end = Dimensions.Padding.Small,
                    top = if (isMultiline) Dimensions.Padding.Small else Dimensions.Padding.None,
                    bottom = if (isMultiline) Dimensions.Padding.Small else Dimensions.Padding.None
                )
                .weight(1f),
            value = messageString,
            onValueChange = { newText: String ->
                onMessageChange(newText)
            },
            onTextLayout = { textLayoutResult ->
                isMultiline = textLayoutResult.lineCount > 1
            },
            singleLine = false,
            maxLines = 6,
            textStyle = JaemTextStyle(MaterialTheme.typography.titleMedium),
            cursorBrush = SolidColor(JAEMThemeProvider.current.accent),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = Dimensions.Size.Medium)
                        .clip(Dimensions.Shape.Rounded.Small)
                ) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimensions.Padding.Small)
                    ) {
                        // Platzhalter
                        if (messageString.isEmpty()) {
                            Text(
                                text = stringResource(R.string.message),
                                style = JaemTextStyle(
                                    MaterialTheme.typography.titleMedium,
                                    color = JAEMThemeProvider.current.textSecondary
                                )
                            )
                        }
                        innerTextField()
                    }
                    // Anhänge
                    AnimatedVisibility(
                        visible = messageString.isEmpty(),
                        enter = slideInHorizontally { fullWidth -> fullWidth },
                        exit = slideOutHorizontally { fullWidth -> fullWidth }
                    ) {
                        Row {
                            IconButton(onClick = onClickAttache) {
                                Icon(
                                    Icons.Outlined.Attachment,
                                    contentDescription = stringResource(R.string.attachment_bt),
                                    tint = JAEMThemeProvider.current.textSecondary
                                )
                            }
                            IconButton(onClick = onClickCamera) {
                                Icon(
                                    Icons.Outlined.CameraAlt,
                                    contentDescription = stringResource(R.string.camera_bt),
                                    tint = JAEMThemeProvider.current.textSecondary
                                )
                            }
                        }
                    }
                }
            },
        )
        AnimatedVisibility(
            visible = messageString.isNotEmpty(),
        ) {
            // Senden
            IconButton(
                modifier = Modifier
                    .padding(start = Dimensions.Padding.Small)
                    .background(
                        color = JAEMThemeProvider.current.primary,
                        shape = Dimensions.Shape.Rounded.Small
                    ),
                onClick = {
                    onSendMessage(messageString)
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send_message_bt),
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        }
    }
}