package de.stubbe.jaem_client.view.screens.chat

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.utils.loadPreviewFromFile
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.components.ExtensionPresentation
import de.stubbe.jaem_client.view.components.LoadingIfNull
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    attachments: Attachments?,
    onClickEncryption: () -> Unit,
    onClickAttache: () -> Unit,
    onRemoveAttachment: () -> Unit,
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
            textStyle = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                fontSize = Dimensions.FontSize.Medium
            ),
            cursorBrush = SolidColor(JAEMThemeProvider.current.accent),
            decorationBox = { innerTextField ->
                Column {
                    AnimatedVisibility(
                        visible = attachments != null,
                    ) {
                        Column {
                            if (attachments != null) {
                                Attachment(
                                    attachments = attachments,
                                    onRemoveAttachment = onRemoveAttachment
                                )
                            }

                            Divider()
                        }
                    }

                    TextField(
                        messageString = messageString,
                        isMultiline = isMultiline,
                        onClickEncryption = onClickEncryption,
                        onClickAttache = onClickAttache,
                        innerTextField = innerTextField,
                    )
                }
            },
        )
        AnimatedVisibility(
            visible = messageString.isNotEmpty() || attachments != null,
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

@Composable
private fun Attachment(
    attachments: Attachments,
    onRemoveAttachment: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.Padding.Small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (attachments.type == AttachmentType.FILE) {
            val file = remember(attachments) { attachments.attachmentPaths.firstOrNull()?.let { File(it) } }

            LoadingIfNull(file) {
                ExtensionPresentation(
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.Tiny),
                    extension = file!!.extension
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = file.name,
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleSmall,
                        color = JAEMThemeProvider.current.textSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.Tiny)
            ) {
                items(attachments.attachmentPaths) { filePath ->
                    var bitmap: Bitmap? by remember { mutableStateOf(null) }

                    LaunchedEffect(Unit) {
                        launch(Dispatchers.IO) {
                            bitmap = File(filePath).loadPreviewFromFile(context)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(Dimensions.Size.Medium)
                            .clip(Dimensions.Shape.Rounded.Small)
                            .padding(Dimensions.Padding.Tiny),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIfNull(bitmap) {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(1f)
                                    .clip(Dimensions.Shape.Rounded.Small),
                                bitmap = bitmap!!.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = {
                onRemoveAttachment()
            }
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = stringResource(R.string.delete),
                tint = JAEMThemeProvider.current.textSecondary
            )
        }
    }
}

@Composable
private fun TextField(
    messageString: String,
    isMultiline: Boolean,
    onClickEncryption: () -> Unit,
    onClickAttache: () -> Unit,
    innerTextField: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = Dimensions.Size.Medium)
            .clip(Dimensions.Shape.Rounded.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.Bottom),
            onClick = onClickEncryption
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = stringResource(R.string.encryption_bt),
                tint = JAEMThemeProvider.current.textSecondary
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(
                    end = Dimensions.Padding.Small,
                    top = if (isMultiline) Dimensions.Padding.Small else Dimensions.Padding.None,
                    bottom = if (isMultiline) Dimensions.Padding.Small else Dimensions.Padding.None
                )
        ) {
            // Platzhalter
            if (messageString.isEmpty()) {
                Text(
                    text = stringResource(R.string.message),
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleMedium,
                        color = JAEMThemeProvider.current.textSecondary
                    ).copy(
                        fontSize = Dimensions.FontSize.Medium
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
            IconButton(onClick = onClickAttache) {
                Icon(
                    Icons.Outlined.Attachment,
                    contentDescription = stringResource(R.string.attachment_bt),
                    tint = JAEMThemeProvider.current.textSecondary
                )
            }
        }
    }
}