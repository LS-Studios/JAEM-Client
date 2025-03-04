package de.stubbe.jaem_client.view.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    profilePicture: Bitmap?
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(JAEMThemeProvider.current.primary),
        contentAlignment = Alignment.Center
    ) {
        if (profilePicture != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = profilePicture.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.Padding.Medium),
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = JAEMThemeProvider.current.textPrimary
            )
        }
    }
}