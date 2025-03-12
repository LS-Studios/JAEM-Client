package de.stubbe.jaem_client.view.components.topbars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleWithActionsTopBar(
    title: String,
    leadingActions: @Composable (() -> Unit)? = null,
    trailingActions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge).copy(
                    textAlign = if (trailingActions != null && leadingActions != null) {
                        TextAlign.Center
                    } else {
                        TextAlign.Start
                    }
                )
            )
        },
        navigationIcon = {
            leadingActions?.invoke()
        },
        actions = {
            trailingActions?.invoke()
        },
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = JAEMThemeProvider.current.background),
    )
}