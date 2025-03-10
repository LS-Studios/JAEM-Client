package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.data.JAEMTextStyle

@Composable
fun NoItemsText(
    text: String
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = JAEMTextStyle(MaterialTheme.typography.titleMedium, alpha = 0.5f)
            .copy(
                fontStyle = FontStyle.Italic
            ),
        textAlign = TextAlign.Center,
    )
}