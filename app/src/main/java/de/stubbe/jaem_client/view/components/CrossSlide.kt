package de.stubbe.jaem_client.view.components

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Eine Verschiebung der Inhalte von einem Zustand zum anderen.
 * @param targetState Der Zielzustand, zu dem die Inhalte verschoben werden sollen
 * @param modifier Der Modifier, der auf den Container angewendet wird.
 * @param animationSpec Die Animationsspezifikation, die auf die Verschiebung angewendet wird.
 * @param reverseAnimation Ob die Animation umgekehrt werden soll.
 * @param alternateDirection Ob die Richtung der Animation alterniert werden soll.
 * @param content Der Inhalt, der verschoben werden soll.
 */
@Composable
fun <T> CrossSlide(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Offset> = tween(500),
    reverseAnimation: Boolean = false,
    alternateDirection: Boolean = false,
    content: @Composable (T) -> Unit
) {
    // Track the current animation direction
    val direction = remember { mutableIntStateOf(if (reverseAnimation) -1 else 1) }
    val items = remember { mutableStateListOf<SlideInOutAnimationState<T>>() }
    val transitionState = remember { MutableTransitionState(targetState) }
    val targetChanged = (targetState != transitionState.targetState)
    transitionState.targetState = targetState
    val transition: Transition<T> = rememberTransition(transitionState)

    if (targetChanged || items.isEmpty()) {
        // Toggle the direction if alternateDirection is enabled
        if (alternateDirection) {
            direction.value *= -1
        }

        // Update the items list with the new target state
        val keys = items.map { it.key }.run {
            if (!contains(targetState)) {
                toMutableList().also { it.add(targetState) }
            } else {
                this
            }
        }
        items.clear()
        keys.mapTo(items) { key ->
            SlideInOutAnimationState(key) {
                val xTransition by transition.animateOffset(
                    transitionSpec = { animationSpec }, label = ""
                ) { if (it == key) Offset(0f, 0f) else Offset(1000f, 1000f) }

                Box(modifier.graphicsLayer {
                    this.translationX =
                        if (transition.targetState == key) direction.intValue * xTransition.x else direction.intValue * -xTransition.x
                }) {
                    content(key)
                }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.key != transitionState.targetState }
    }

    Box(modifier) {
        items.forEach {
            key(it.key) {
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    it.content()
                }
            }
        }
    }
}

private data class SlideInOutAnimationState<T>(
    val key: T,
    val content: @Composable () -> Unit
)

