package de.stubbe.jaem_client.view.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    profilePicture: Bitmap?
) {
    if (profilePicture != null) {
        Image(
            modifier = modifier
                .clip(CircleShape),
            bitmap = profilePicture.asImageBitmap(),
            contentDescription = null
        )
    } else {
        Icon(
            modifier = modifier
                .clip(CircleShape)
                .background(JAEMThemeProvider.current.primary)
                .padding(Dimensions.Padding.Medium),
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = JAEMThemeProvider.current.textPrimary
        )
    }
}