package de.stubbe.jaem_client.utils

import androidx.compose.foundation.lazy.LazyListState

/**
 * Prüft, ob die Liste am Ende gescrollt ist
 *
 * @return true, wenn die Liste am Ende ist
 */
fun LazyListState.isScrolledToEnd(): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= layoutInfo.totalItemsCount - 2
}