package de.stubbe.jaem_client.view.screens.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.ReplyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.data.PROFILE_PICTURE_TRANSITION
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.utils.mirror
import de.stubbe.jaem_client.view.components.CrossSlide
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.ChatViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ChatTopBar(
    navigationViewModel: NavigationViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    chatViewModel: ChatViewModel,
    chat: ChatPresentationModel?,
    onGoBack: () -> Unit,
    canGoNext: Boolean,
    onNextFoundElement: () -> Unit,
    canGoLast: Boolean,
    onLastFoundElement: () -> Unit,
    onDelete: () -> Unit,
) {
    val currentNavRoute by navigationViewModel.getCurrentRouteFlow<NavRoute.ChatMessages>().collectAsState()

    val searchText by chatViewModel.searchValue.collectAsState()
    val selectedMessages by chatViewModel.selectedMessages.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Zuweisen des Fokus auf das Suchfeld, wenn die Suche aktiviert wird
    LaunchedEffect(isSearchActive) {
        chatViewModel.changeSearchValue("")

        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(currentNavRoute) {
        if (currentNavRoute is NavRoute.ChatMessages && (currentNavRoute as NavRoute.ChatMessages).searchEnabled) {
            isSearchActive = true
        }
    }

    // Schließen der Suche, wenn der Zurück-Action ausgeführt wird
    BackHandler(isSearchActive) {
        isSearchActive = false
    }

    AnimatedContent(
        targetState = selectedMessages.isEmpty(),
    ) { selectedMessagedTargetState ->
        if (selectedMessagedTargetState) {
            // Animation zur Anzeige des Suchfeldes
            CrossSlide(
                modifier = Modifier
                    .height(Dimensions.Size.TopBar),
                targetState = isSearchActive,
                animationSpec = tween(200),
                alternateDirection = true
            ) { crossSlideTargetState ->
                if (crossSlideTargetState) {
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
                            chatViewModel.changeSearchValue(newText)
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
                                            text = stringResource(R.string.search_for_message),
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
                                IconButton(
                                    modifier = Modifier
                                        .alpha(if (canGoLast) 1f else 0.5f),
                                    onClick = {
                                        onLastFoundElement()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowUp,
                                        contentDescription = stringResource(R.string.last_found_element_bt),
                                        tint = JAEMThemeProvider.current.textPrimary
                                    )
                                }
                                IconButton(
                                    modifier = Modifier
                                        .alpha(if (canGoNext) 1f else 0.5f),
                                    onClick = {
                                        onNextFoundElement()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = stringResource(R.string.next_found_element_bt),
                                        tint = JAEMThemeProvider.current.textPrimary
                                    )
                                }
                            }
                        },
                    )
                } else {
                    // TopBar mit Bild, Name und Actionen
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = null,
                                        indication = ripple(
                                            bounded = true
                                        )
                                    ) {
                                        // Disable search enable in case its enabled
                                        navigationViewModel.updateScreenArguments<NavRoute.ChatMessages> {
                                            copy(searchEnabled = false)
                                        }

                                        // Navigate to the profile screen
                                        navigationViewModel.navigateTo(
                                            NavRoute.Profile
                                        )
                                    },
                                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //TODO Crash on rotate
                                with(sharedTransitionScope) {
                                    // Bild
                                    ProfilePicture(
                                        modifier = Modifier
                                            .size(Dimensions.Size.Small)
                                            .then(
                                                if (!isSearchActive) {
                                                    Modifier.sharedElement(
                                                        rememberSharedContentState(key = PROFILE_PICTURE_TRANSITION),
                                                        animatedVisibilityScope = animatedVisibilityScope
                                                    )
                                                } else {
                                                    Modifier
                                                }
                                            ),
                                        profilePicture = chat?.profilePicture
                                    )
                                }

                                // Name
                                Text(
                                    text = chat?.name ?: "",
                                    style = JAEMTextStyle(MaterialTheme.typography.titleLarge),
                                )
                            }
                        },
                        navigationIcon = {
                            // Zurück-Action
                            IconButton(onClick = onGoBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back_bt),
                                    tint = JAEMThemeProvider.current.textPrimary
                                )
                            }
                        },
                        actions = {
                            // Such-Action
                            IconButton(onClick = {
                                isSearchActive = true
                            }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = stringResource(R.string.search_for_element_bt),
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
        else {
            // Aktionen für ausgewählte Nachrichten
            TopAppBar(
                modifier = Modifier
                    .height(Dimensions.Size.TopBar)
                    .padding(Dimensions.Padding.TopBar),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = "${selectedMessages.size}",
                            style = JAEMTextStyle(MaterialTheme.typography.titleLarge),
                        )
                    }
                },
                navigationIcon = {
                    // Zurück-Action
                    IconButton(onClick = {
                        chatViewModel.changeSelectedMessages(emptyList())
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_bt),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                },
                actions = {
                    // Antworten-Action
                    IconButton(onClick = {

                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Reply,
                            contentDescription = stringResource(R.string.reply_bt),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                    // Lösch-Action
                    IconButton(onClick = {
                        onDelete()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                    // Weiterleiten-Action
                    IconButton(onClick = {

                    }) {
                        Icon(
                            modifier = Modifier.mirror(),
                            imageVector = Icons.AutoMirrored.Filled.ReplyAll,
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