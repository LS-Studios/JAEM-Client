package de.stubbe.jaem_client.view.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap

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
        Image(
            modifier = modifier
                .clip(CircleShape),
            imageVector = Icons.Default.Person,
            contentDescription = null
        )
    }
}