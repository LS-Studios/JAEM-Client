package de.stubbe.jaem_client.view.screens.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.utils.formatTime
import de.stubbe.jaem_client.utils.toBitmap
import de.stubbe.jaem_client.utils.toLocalDateTime
import de.stubbe.jaem_client.view.components.HighlightText
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageBubble(
    message: MessageModel,
    firstMessageOfBlock: Boolean,
    isSentByUser: Boolean,
    searchValue: String,
    isSelected: Boolean,
    onSelect: (MessageModel) -> Unit,
) {
    val localDensity = LocalDensity.current
    val bubbleColor = JAEMThemeProvider.current.secondary
    val timeWidth = 80 // Breite für die Zeitstempelanzeige
    val maxBubbleWidth = (LocalConfiguration.current.screenWidthDp.dp * 3 / 4) + timeWidth.dp

    // Zustände für das Layout-Verhalten
    var reachedMaxBubbleWidth by remember { mutableStateOf(true) }
    var spaceLeft by remember { mutableStateOf(false) }
    var lineCount by remember { mutableStateOf(0) }
    var isTimeTextInline by remember { mutableStateOf(false) }

    val file = remember(message) { message.filePath?.let { File(it) } }
    val bitmap = remember(file) { file?.toBitmap() }

    // Bestimmen, ob der Zeitstempel inline angezeigt werden soll
    LaunchedEffect(reachedMaxBubbleWidth, spaceLeft) {
        isTimeTextInline = reachedMaxBubbleWidth || spaceLeft
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .combinedClickable(
                onClick = {},
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
                // Bild oder Datei anzeigen
                when {
                    file != null && bitmap != null -> Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                            .clip(Dimensions.Shape.Rounded.Small)
                    )

                    file != null -> Text(
                        modifier = Modifier.padding(bottom = Dimensions.Padding.Tiny),
                        text = file.name,
                        style = JAEMTextStyle(MaterialTheme.typography.titleMedium)
                    )
                }

                // Nachrichteninhalt mit Zeitstempel in einer Zeile
                if (isTimeTextInline) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        HighlightText(
                            modifier = Modifier.widthIn(max = maxBubbleWidth - timeWidth.dp),
                            text = message.stringContent ?: "",
                            highlight = searchValue,
                            style = JAEMTextStyle(MaterialTheme.typography.titleMedium),
                            onTextLayout = { textLayoutResult ->
                                lineCount = textLayoutResult.lineCount
                            }
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = Dimensions.Padding.Small)
                                .offset(y = Dimensions.Spacing.Small),
                            text = message.sendTime.toLocalDateTime().formatTime(),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.End
                        )
                    }
                }
                // Zeitstempel unter dem Nachrichtentext anzeigen
                else {
                    Column(horizontalAlignment = Alignment.End) {
                        HighlightText(
                            modifier = Modifier.padding(bottom = Dimensions.Padding.Tiny),
                            text = message.stringContent ?: "",
                            highlight = searchValue,
                            style = JAEMTextStyle(MaterialTheme.typography.titleMedium),
                            onTextLayout = { textLayoutResult ->
                                val lastLineIndex = textLayoutResult.lineCount - 1
                                val lastLineEnd = textLayoutResult.getLineRight(lastLineIndex)
                                val totalWidth = textLayoutResult.size.width
                                val leftSpace = totalWidth - lastLineEnd
                                spaceLeft = leftSpace > 200
                            }
                        )
                        Text(
                            text = message.sendTime.toLocalDateTime().formatTime(),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.End
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
                    .background(JAEMThemeProvider.current.accent.copy(alpha = 0.08f))
            )
        }
    }
}