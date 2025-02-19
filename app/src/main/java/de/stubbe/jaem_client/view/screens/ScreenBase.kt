package de.stubbe.jaem_client.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.view.components.Divider

/**
 * Basis für einen Bildschirm
 *
 * @param topBar Top Bar des Bildschirms
 * @param content Inhalt des Bildschirms
 */
@Composable
fun ScreenBase(
    topBar: @Composable () -> Unit,
    useDivider: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        topBar()

        if (useDivider) {
            Divider()
        }

        content()
    }
}