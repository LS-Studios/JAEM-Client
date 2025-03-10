package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JAEMPullToRefresh(
    modifier: Modifier = Modifier,
    refreshing: Boolean,
    onRefresh: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var isRefreshingWorkaround by remember { mutableStateOf(refreshing) }

    LaunchedEffect(refreshing) {
        isRefreshingWorkaround = refreshing
    }

    val state = rememberPullToRefreshState()

    Box(
        modifier = modifier.pullToRefresh(
            isRefreshing = isRefreshingWorkaround,
            state = state,
            onRefresh = onRefresh
        )
    ) {
        content()
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshingWorkaround,
            state = state,
            containerColor = JAEMThemeProvider.current.primary,
            color = JAEMThemeProvider.current.textPrimary
        )
    }
}