package de.stubbe.jaem_client.view.screens.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.utils.formatTime
import de.stubbe.jaem_client.utils.toLocalDateTime
import de.stubbe.jaem_client.utils.toSizeString
import de.stubbe.jaem_client.view.components.ExtensionPresentation
import de.stubbe.jaem_client.view.components.HighlightText
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import java.io.File
import kotlin.io.path.fileSize

const val timeWidth = 80

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageBubble(
    message: MessageModel,
    firstMessageOfBlock: Boolean,
    isSentByUser: Boolean,
    searchValue: String,
    selectionEnabled: Boolean,
    isSelected: Boolean,
    onSelect: (MessageModel) -> Unit,
) {
    val localDensity = LocalDensity.current
    val bubbleColor = JAEMThemeProvider.current.secondary
    val maxBubbleWidth = (LocalConfiguration.current.screenWidthDp.dp * 3 / 4) + timeWidth.dp

    // Zustände für das Layout-Verhalten
    var reachedMaxBubbleWidth by remember { mutableStateOf(true) }
    var spaceLeft by remember { mutableStateOf(false) }
    var lineCount by remember { mutableStateOf(0) }
    var isTimeTextInline by remember { mutableStateOf(false) }

//    val files = remember(message) { message.filePaths.map { File(it) } }
//    val bitmaps = remember(file) { file?.toBitmap() }

    // Bestimmen, ob der Zeitstempel inline angezeigt werden soll
    LaunchedEffect(reachedMaxBubbleWidth, spaceLeft) {
        isTimeTextInline = reachedMaxBubbleWidth || spaceLeft
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .combinedClickable(
                onClick = {
                    if (selectionEnabled) {
                        onSelect(message)
                    }
                },
                onLongClick = {
                    onSelect(message)
                },
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                )
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimensions.Padding.Small,
                    vertical = Dimensions.Padding.Tiny
                ),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            // Dreieck links für empfangene Nachrichten
            if (!isSentByUser) {
                Box(
                    modifier = Modifier
                        .alpha(if (firstMessageOfBlock) 1f else 0f)
                        .background(
                            color = bubbleColor,
                            shape = Dimensions.Shape.ChatBubbleShape.Left.Triangle
                        )
                        .width(Dimensions.Size.SuperTiny)
                        .fillMaxHeight()
                )
            }

            Column(
                modifier = Modifier
                    .background(
                        bubbleColor,
                        shape = when {
                            firstMessageOfBlock && !isSentByUser -> Dimensions.Shape.ChatBubbleShape.Left.Body
                            firstMessageOfBlock && isSentByUser -> Dimensions.Shape.ChatBubbleShape.Right.Body
                            else -> Dimensions.Shape.Rounded.Small
                        }
                    )
                    .padding(8.dp)
                    .widthIn(max = if (lineCount == 0) maxBubbleWidth - timeWidth.dp else maxBubbleWidth)
                    .onSizeChanged { size ->
                        with(localDensity) {
                            reachedMaxBubbleWidth = (timeWidth + size.width) < maxBubbleWidth.toPx()
                        }
                    }
            ) {

                if (isTimeTextInline) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        MessageContent(
                            isTimeTextInline,
                            message,
                            searchValue,
                            maxBubbleWidth,
                            setLineCount = { lineCount = it },
                            setSpaceLeft = { spaceLeft = it }
                        )
                    }
                }
                // Zeitstempel unter dem Nachrichtentext anzeigen
                else {
                    Column(horizontalAlignment = Alignment.End) {
                        MessageContent(
                            isTimeTextInline,
                            message,
                            searchValue,
                            maxBubbleWidth,
                            setLineCount = { lineCount = it },
                            setSpaceLeft = { spaceLeft = it }
                        )
                    }
                }
            }

            // Dreieck rechts für gesendete Nachrichten
            if (isSentByUser) {
                Box(
                    modifier = Modifier
                        .alpha(if (firstMessageOfBlock) 1f else 0f)
                        .background(
                            color = bubbleColor,
                            shape = Dimensions.Shape.ChatBubbleShape.Right.Triangle
                        )
                        .width(Dimensions.Size.SuperTiny)
                        .fillMaxHeight()
                )
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
                    .background(JAEMThemeProvider.current.accent.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun MessageContent(
    itTimeTextInline: Boolean,
    message: MessageModel,
    searchValue: String,
    maxBubbleWidth: Dp,
    setLineCount: (Int) -> Unit,
    setSpaceLeft: (Boolean) -> Unit,
) {
    if (message.attachments != null) {

    } else {

    }
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.Tiny)
    ) {
        if (message.attachments != null) {
            AttachmentsPresentation(
                attachments = message.attachments
            )
        }
        if (message.stringContent?.isNotEmpty() == true) {
            HighlightText(
                modifier = Modifier.widthIn(max = maxBubbleWidth - timeWidth.dp),
                text = message.stringContent ?: "",
                highlight = searchValue,
                style = JAEMTextStyle(MaterialTheme.typography.titleMedium),
                onTextLayout = { textLayoutResult ->
                    if (itTimeTextInline) {
                        setLineCount(textLayoutResult.lineCount)
                    } else {
                        val lastLineIndex = textLayoutResult.lineCount - 1
                        val lastLineEnd =
                            textLayoutResult.getLineRight(lastLineIndex)
                        val totalWidth = textLayoutResult.size.width
                        val leftSpace = totalWidth - lastLineEnd
                        setSpaceLeft(leftSpace > 200)
                    }
                }
            )
        }
    }
    Text(
        modifier = Modifier
            .then(
                if (itTimeTextInline) {
                    Modifier
                        .padding(start = Dimensions.Padding.Small)
                        .offset(y = Dimensions.Spacing.Small)
                } else {
                    Modifier.padding(bottom = Dimensions.Padding.Tiny)
                }
            ),
        text = message.sendTime.toLocalDateTime().formatTime(),
        fontSize = 12.sp,
        color = Color.Gray,
        textAlign = TextAlign.End
    )
}

@Composable
private fun AttachmentsPresentation(
    attachments: Attachments
) {
    Box(
        modifier = Modifier
            .background(
                color = JAEMThemeProvider.current.primary,
                shape = Dimensions.Shape.Rounded.Small
            )
            .padding(Dimensions.Padding.Small)
    ) {
        when (attachments.type) {
            AttachmentType.FILE -> {
                val file = attachments.attachmentPaths.firstOrNull()
                    ?.let { File(it) }

                if (file != null) {
                    Column {
                        Row {
                            ExtensionPresentation(
                                extension = file.extension
                            )
                            Text(
                                text = file.name,
                                style = JAEMTextStyle(MaterialTheme.typography.titleMedium)
                            )
                        }
                        Text(
                            text = file.toPath().fileSize().toSizeString(),
                            style = JAEMTextStyle(MaterialTheme.typography.labelSmall)
                        )
                    }
                }
            }

            AttachmentType.IMAGE_AND_VIDEO -> {
                Text(
                    text = "Bild",
                    style = JAEMTextStyle(MaterialTheme.typography.titleMedium)
                )
            }
        }
    }
}