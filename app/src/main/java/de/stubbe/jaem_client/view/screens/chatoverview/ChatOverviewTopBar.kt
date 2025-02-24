package de.stubbe.jaem_client.view.screens.chatoverview

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.view.components.CrossSlide
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.view.variables.RaviPrakash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatOverviewTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
) {
    var isSearchActive by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Zuweisen des Fokus auf das Suchfeld, wenn die Suche aktiviert wird
    LaunchedEffect(isSearchActive) {
        onSearchTextChange("")

        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    // Schließen der Suche, wenn der Zurück-Action ausgeführt wird
    BackHandler(isSearchActive) {
        isSearchActive = false
    }

    // Animation zur Anzeige des Suchfeldes
    CrossSlide(
        modifier = Modifier
            .height(56.dp),
        targetState = isSearchActive,
        animationSpec = tween(200),
        alternateDirection = true
    ) { targetState ->
        if (targetState) {
            // Suchfeld
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .background(
                        color = JAEMThemeProvider.current.secondary,
                        shape = CircleShape
                    ),
                value = searchText,
                onValueChange = { newText: String ->
                    onSearchTextChange(newText)
                },
                singleLine = true,
                textStyle = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                    fontSize = Dimensions.FontSize.Medium
                ),
                cursorBrush = SolidColor(JAEMThemeProvider.current.accent),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { isSearchActive = false }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_bt),
                                tint = JAEMThemeProvider.current.textPrimary
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_for_chat),
                                    style = JAEMTextStyle(
                                        MaterialTheme.typography.titleMedium,
                                        color = JAEMThemeProvider.current.textSecondary
                                    ).copy(
                                        fontSize = Dimensions.FontSize.Medium
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                },
            )
        } else {
            // TopBar mit Such- und Mehr-Action
            TopAppBar(
                title = {
                    // Name der App
                    Text(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = 12f
                            },
                        text = stringResource(R.string.app_name),
                        style = JAEMTextStyle(MaterialTheme.typography.headlineLarge, fontFamily = RaviPrakash),
                    )
                },
                actions = {
                    // Such-Action
                    IconButton(onClick = {
                        isSearchActive = true
                    }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.more_actions_bt),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }

                    // Mehr-Action
                    IconButton(onClick = {

                    }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_actions_bt),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JAEMThemeProvider.current.background),
            )
        }
    }
}