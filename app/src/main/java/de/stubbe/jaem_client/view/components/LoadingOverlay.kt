package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = JAEMThemeProvider.current.textPrimary,
                trackColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f)
            )
        }
    }
}