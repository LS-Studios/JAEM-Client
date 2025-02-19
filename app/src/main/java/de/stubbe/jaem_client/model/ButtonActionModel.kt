package de.stubbe.jaem_client.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Model for a button action with an icon, content description, alignment, sub actions and an onClick function.
 * @param icon The icon of the button action.
 * @param contentDescription The content description of the button action.
 * @param alignment The alignment of the button action.
 * @param subActions The sub actions of the button action.
 * @param onClick The onClick function of the button action. If empty sub actions are shown.
 */
data class ButtonActionModel(
    val text: String? = null,
    val icon: ImageVector,
    val contentDescription: String,
    val alignment: Alignment = Alignment.BottomEnd,
    val subActions: List<ButtonActionModel> = emptyList(),
    val onClick: () -> Unit = {}
)
