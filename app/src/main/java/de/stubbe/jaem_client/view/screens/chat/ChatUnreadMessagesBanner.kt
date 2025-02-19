package de.stubbe.jaem_client.view.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.view.variables.JaemTextStyle

@Composable
fun ChatUnreadMessagesBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(JAEMThemeProvider.current.primary.copy(alpha = 0.5f))
            .padding(Dimensions.Padding.Small)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    JAEMThemeProvider.current.primary,
                    shape = Dimensions.Shape.Rounded.Medium
                )
                .padding(
                    vertical = Dimensions.Padding.Small,
                    horizontal = Dimensions.Padding.Medium
                ),
            text = stringResource(R.string.new_messages),
            style = JaemTextStyle(MaterialTheme.typography.titleSmall),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}