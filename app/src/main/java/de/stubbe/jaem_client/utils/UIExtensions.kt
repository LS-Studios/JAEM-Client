package de.stubbe.jaem_client.utils

import android.util.LayoutDirection
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.core.text.layoutDirection
import androidx.navigation.NavBackStackEntry
import de.stubbe.jaem_client.model.NavRoute
import java.util.Locale
import kotlin.reflect.full.createInstance

/**
 * Prüft, ob die Liste am Ende gescrollt ist
 *
 * @return true, wenn die Liste am Ende ist
 */
fun LazyListState.isScrolledToEnd(): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= layoutInfo.totalItemsCount - 2
}

inline fun <reified T> NavBackStackEntry.isNavRouteOfType(): Boolean {
    val route = this.destination.route ?: return false

    val navRoute = NavRoute::class.sealedSubclasses.firstOrNull {
        route.contains(it.qualifiedName.toString())
    }?.let {
        it.objectInstance ?: it.createInstance()
    }

    return navRoute is T
}

fun Modifier.mirror(): Modifier {
    return if (Locale.getDefault().layoutDirection == LayoutDirection.RTL)
        this.scale(scaleX = -1f, scaleY = 1f)
    else
        this
}