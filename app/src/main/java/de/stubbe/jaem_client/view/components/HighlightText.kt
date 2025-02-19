package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightText(
    modifier: Modifier = Modifier,
    text: String,
    highlight: String,
    highlightTextColor: Color = Color.Black,
    highlightBackgroundColor: Color = Color.White,
    style: TextStyle = LocalTextStyle.current,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    if (highlight.isEmpty()) {
        BasicText(
            modifier = modifier,
            text = text,
            style = style,
            onTextLayout = onTextLayout
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        var startIndex = 0
        val highlightLength = highlight.length
        val lowerCaseFullText = text.lowercase()
        val lowerCaseSearchText = highlight.lowercase()

        while (startIndex < text.length) {
            val index = lowerCaseFullText.indexOf(lowerCaseSearchText, startIndex)
            if (index == -1) {
                append(text.substring(startIndex))
                break
            } else {
                append(text.substring(startIndex, index))

                withStyle(
                    style = SpanStyle(
                        color = highlightTextColor,
                        background = highlightBackgroundColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(text.substring(index, index + highlightLength))
                }
                startIndex = index + highlightLength
            }
        }
    }

    BasicText(
        modifier = modifier,
        text = annotatedString,
        style = style,
        onTextLayout = onTextLayout
    )
}
