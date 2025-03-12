package de.stubbe.jaem_client.view.components

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.semantics.Role
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.ButtonActionModel
import de.stubbe.jaem_client.utils.horizontal
import de.stubbe.jaem_client.utils.vertical
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import kotlinx.coroutines.delay

/**
 * Aktionen die am unteren Bildschirmrand angezeigt werden können.
 *
 * @param actions Liste von Aktionen
 * @param animationSpeed Geschwindigkeit der Erscheinugsanimation
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ButtonActions(
    modifier: Modifier = Modifier,
    actions: List<ButtonActionModel>,
    animationSpeed: Long = 100L
) {
    var expandedAction by remember { mutableStateOf<ButtonActionModel?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInteropFilter {
                if (it.action == MotionEvent.ACTION_DOWN) {
                    expandedAction = null
                }
                false
            }
    ) {
        actions.forEach { action ->
            /**
             * Anzeige der Subaktionen.
             *
             * @param isBottomAligned Ob die Subaktionen am unteren Bildschirmrand angezeigt werden
             */
            @Composable
            fun getSubActionButtons(
                isBottomAligned: Boolean,
                alignment: Alignment.Horizontal
            ) {
                val visibleStates = remember { mutableStateListOf<Boolean>() }

                // Gestapelte Animation der Subaktionen
                LaunchedEffect(expandedAction) {
                    if (action.subActions.isEmpty()) return@LaunchedEffect

                    if (expandedAction != action) {
                        visibleStates.clear()
                        visibleStates.addAll(List(action.subActions.size) { false })
                        return@LaunchedEffect
                    }

                    visibleStates.clear()
                    visibleStates.addAll(List(action.subActions.size) { false })

                    action.subActions.forEachIndexed { index, _ ->
                        visibleStates[index] = true
                        delay(animationSpeed)
                    }
                }

                Column(
                    modifier = Modifier
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                    horizontalAlignment = alignment
                ) {
                    action.subActions.forEachIndexed { index, subAction ->
                        // Animation der Subaktionen
                        AnimatedVisibility(
                            visible = visibleStates.getOrNull(index) == true,
                            enter = slideInVertically(
                                initialOffsetY = { if (isBottomAligned) it else -it }
                            ) + fadeIn(),
                            exit = slideOutVertically(
                                targetOffsetY = { if (isBottomAligned) it else -it }
                            ) + fadeOut()
                        ) {
                            ActionButton(
                                modifier = Modifier.padding(Dimensions.Padding.Small),
                                text = subAction.text,
                                alignment = action.alignment.horizontal(),
                                icon = subAction.icon,
                                contentDescription = subAction.contentDescription,
                                onClick = {
                                    subAction.onClick()
                                    expandedAction = null
                                },
                            )
                        }
                    }
                }
            }

            when (action.alignment.vertical()) {
                Alignment.Bottom -> {
                    Column(
                        modifier = Modifier
                            .align(action.alignment),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                        horizontalAlignment = action.alignment.horizontal()
                    ) {
                        getSubActionButtons(true, action.alignment.horizontal())
                        ActionButton(
                            modifier = Modifier.padding(Dimensions.Padding.Medium),
                            icon = action.icon,
                            contentDescription = action.contentDescription,
                            onClick = {
                                if (action.subActions.isEmpty()) {
                                    action.onClick()
                                    expandedAction = null
                                } else {
                                    expandedAction = if (expandedAction == action) null else action
                                }
                            },
                        )
                    }
                }

                Alignment.Top -> {
                    Column(
                        modifier = Modifier
                            .align(action.alignment)
                            .animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                        horizontalAlignment = action.alignment.horizontal()
                    ) {
                        ActionButton(
                            modifier = Modifier.padding(Dimensions.Padding.Medium),
                            icon = action.icon,
                            contentDescription = action.contentDescription,
                            onClick = {
                                if (action.subActions.isEmpty()) {
                                    action.onClick()
                                    expandedAction = null
                                } else {
                                    expandedAction = if (expandedAction == action) null else action
                                }
                            },
                        )
                        getSubActionButtons(false, action.alignment.horizontal())
                    }
                }
            }
        }
    }
}

/**
 * Button für Aktionen.
 *
 * @param text Text des Buttons
 * @param icon Icon des Buttons
 * @param contentDescription Beschreibung des Icons
 * @param onClick Funktion die beim Klick ausgeführt wird
 */
@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.run {
            background(
                    JAEMThemeProvider.current.primary,
                    Dimensions.Shape.Rounded.Small
                )
                .border(
                    Dimensions.Border.ThinBorder,
                    JAEMThemeProvider.current.border,
                    Dimensions.Shape.Rounded.Small
                )
                .clickable(
                    onClick = onClick,
                    role = Role.Button,
                    interactionSource = null,
                    indication = ripple(
                        bounded = true,
                    )
                )
                .then(modifier)
        },
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (alignment) {
            Alignment.Start -> {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = JAEMThemeProvider.current.textPrimary
                )
                if (text != null) {
                    Text(
                        text = text,
                        style = JAEMTextStyle(MaterialTheme.typography.titleMedium)
                    )
                }
            }
            else -> {
                if (text != null) {
                    Text(
                        text = text,
                        style = JAEMTextStyle(MaterialTheme.typography.titleMedium)
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        }
    }
}