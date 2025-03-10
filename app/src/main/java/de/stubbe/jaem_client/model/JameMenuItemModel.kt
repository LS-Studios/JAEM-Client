package de.stubbe.jaem_client.model

import androidx.compose.ui.graphics.vector.ImageVector

data class JameMenuItemModel(
    val title: String,
    val onClick: () -> Unit,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null
)