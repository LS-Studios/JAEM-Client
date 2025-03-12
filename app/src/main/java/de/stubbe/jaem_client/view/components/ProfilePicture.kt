package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    profilePicture: ByteArray?,
    showPlaceholder: Boolean = true
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(JAEMThemeProvider.current.primary),
        contentAlignment = Alignment.Center
    ) {
        if (profilePicture == null && showPlaceholder) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.profile_picture_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context)
                    .data(profilePicture)
                    .build(),
                placeholder = painterResource(R.drawable.profile_picture_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}