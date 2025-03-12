package de.stubbe.jaem_client.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

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
    scrollable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JAEMThemeProvider.current.background)
    ) {
        topBar()

        if (useDivider) {
            Divider()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (scrollable) Modifier
                        .verticalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max)
                    else Modifier
                )
        ) {
            content()
        }
    }
}