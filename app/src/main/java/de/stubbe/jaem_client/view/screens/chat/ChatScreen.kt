package de.stubbe.jaem_client.view.screens.chat

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.JAEMFileType
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.utils.AppStorageHelper
import de.stubbe.jaem_client.utils.isScrolledToEnd
import de.stubbe.jaem_client.utils.keyboardVisibility
import de.stubbe.jaem_client.view.components.filepicker.JAEMFilePicker
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.viewmodel.ChatViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.SharedChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Bildschirm für einen Chat
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChatScreen(
    navigationViewModel: NavigationViewModel,
    sharedChatViewModel: SharedChatViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    val context = LocalContext.current

    val viewModel: ChatViewModel = hiltViewModel()

    val userProfileId by viewModel.userProfileUid.collectAsState()

    val chat by sharedChatViewModel.chat.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val oldMessages = remember(messages) { messages.filter { it.deliveryTime != null || it.senderUid == userProfileId } }
    val newMessages = remember(messages, userProfileId) { messages.filter { it.deliveryTime == null && it.senderUid != userProfileId } }

    val newMessageString by viewModel.newMessageString.collectAsState()
    val newAttachment by viewModel.newAttachments.collectAsState()

    val selectedMessages by viewModel.selectedMessages.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val isKeyboardOpen by keyboardVisibility()

    val searchValue by viewModel.searchValue.collectAsState()
    val foundItemIndices by viewModel.foundItemIndices.collectAsState()
    val currentFoundItemIndex by viewModel.currentFoundItemIndex.collectAsState()

    val attachmentPickerIsOpen by viewModel.attachmentPickerIsOpen.collectAsState()

    var initialScrolledDown by remember { mutableStateOf(false) }

    // Neue Nachrichten als gelesen markieren wenn der Bildschirm verlassen wird
    DisposableEffect(Unit) {
        onDispose {
            if (listState.isScrolledToEnd()) {
                viewModel.markNewMessageAsDelivered()
            }

            // Löschen der temporären Dateien
            viewModel.changeAttachment(null)
        }
    }

    // Nach unten scrollen bei neuen Nachichten
    LaunchedEffect(messages.size) {
        if (!initialScrolledDown && messages.isNotEmpty()) {
            launch {
                listState.scrollToItem(messages.size)
                initialScrolledDown = true
            }
        }

        if (messages.lastOrNull()?.senderUid == userProfileId) {
            launch {
                listState.animateScrollToItem(messages.size)
            }
        }

        if (listState.isScrolledToEnd(3)) {
            viewModel.markNewMessageAsDelivered()
            launch {
                listState.animateScrollToItem(messages.size)
            }
        }
    }

    // Ans ende scrollen, wenn die Tastatur geöffnet wird und man unten ist
    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen && listState.isScrolledToEnd()) {
            launch {
                delay(250)
                listState.animateScrollToItem(messages.size)
            }
        }
    }

    // Suchen nach Nachichten
    LaunchedEffect(searchValue) {
        if (searchValue.isNotEmpty()) {
            val foundIndices = messages.indices.filter { index ->
                messages[index].stringContent?.contains(searchValue, ignoreCase = true) == true
            }
            viewModel.changeFoundItemIndices(foundIndices)
            viewModel.changeCurrentFoundItemIndex(0)
        }
    }

    // Bei Änderung des aktuellen such index scrollen
    LaunchedEffect(foundItemIndices, currentFoundItemIndex) {
        val scrollToItemIndex = foundItemIndices.getOrNull(currentFoundItemIndex)
        if (scrollToItemIndex != null && scrollToItemIndex in messages.indices) {
            coroutineScope.launch {
                listState.scrollToItem(scrollToItemIndex)
            }
        }
    }

    ScreenBase(
        topBar = {
            ChatTopBar(
                navigationViewModel = navigationViewModel,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                chatViewModel = viewModel,
                chat = chat,
                onGoBack = {
                    navigationViewModel.goBack()
                },
                canGoLast = currentFoundItemIndex > 0,
                onLastFoundElement = {
                    val newIndex =  currentFoundItemIndex - 1
                    if (newIndex >= 0) {
                        viewModel.changeCurrentFoundItemIndex(newIndex)
                    }
                },
                canGoNext = currentFoundItemIndex < foundItemIndices.size - 1,
                onNextFoundElement = {
                    val newIndex = currentFoundItemIndex + 1
                    if (newIndex < foundItemIndices.size) {
                        viewModel.changeCurrentFoundItemIndex(newIndex)
                    }
                },
                onDelete = {
                    viewModel.deleteSelectedMessages()
                }
            )
        },
        scrollable = false
    ) {
        Column {
            // Nachrichten
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = Dimensions.Padding.Small,
                    ),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small)
            ) {
                itemsIndexed(oldMessages) { index, message ->
                    val firstMessageOfBlock = when {
                        index == 0 -> true
                        messages[index - 1].senderUid != message.senderUid -> true
                        else -> false
                    }

                    ChatMessageBubble(
                        message = message,
                        firstMessageOfBlock = firstMessageOfBlock,
                        isSentByUser = message.senderUid == userProfileId,
                        searchValue = searchValue,
                        selectionEnabled = selectedMessages.isNotEmpty(),
                        isSelected = selectedMessages.contains(message),
                        onSelect = { selectedMessage ->
                            viewModel.changeSelectedMessages(
                                if (selectedMessages.contains(selectedMessage)) {
                                    selectedMessages - selectedMessage
                                } else {
                                    selectedMessages + selectedMessage
                                }
                            )
                        }
                    )
                }
                if (newMessages.isNotEmpty()) {
                    item {
                        ChatUnreadMessagesBanner()
                    }
                }
                itemsIndexed(newMessages) { index, message ->
                    val firstMessageOfBlock = when {
                        index == 0 -> true
                        messages[index - 1].senderUid != message.senderUid -> true
                        else -> false
                    }

                    ChatMessageBubble(
                        message = message,
                        firstMessageOfBlock = firstMessageOfBlock,
                        isSentByUser = message.senderUid == userProfileId,
                        searchValue = searchValue,
                        selectionEnabled = selectedMessages.isNotEmpty(),
                        isSelected = selectedMessages.contains(message),
                        onSelect = { selectedMessage ->
                            viewModel.changeSelectedMessages(
                                if (selectedMessages.contains(selectedMessage)) {
                                    selectedMessages - selectedMessage
                                } else {
                                    selectedMessages + selectedMessage
                                }
                            )
                        }
                    )
                }
            }

            // Eingabefeld
            ChatInputRow(
                modifier = Modifier
                    .imePadding(),
                newMessageString,
                onMessageChange = {
                    viewModel.changeMessageString(it)
                },
                newAttachment,
                onClickEncryption = {

                },
                onClickAttache = {
                    viewModel.openAttachmentPicker()
                },
                onRemoveAttachment = {
                    viewModel.changeAttachment(null)
                },
                onSendMessage = {
                    if (chat != null) {
                        viewModel.markNewMessageAsDelivered()
                        viewModel.sendMessage(chat!!.chat)
                    }
                }
            )
        }
    }

    if (attachmentPickerIsOpen) {
        JAEMFilePicker(
            maxSelection = 100,
            types = listOf(
                JAEMFileType.IMAGE_AND_VIDEO,
                JAEMFileType.STORAGE
            ),
            onDismiss = {
                viewModel.closeAttachmentPicker()
            },
            selected = { type, bytes ->
                viewModel.changeAttachment(null)
                coroutineScope.launch {
                    viewModel.changeAttachment(
                        Attachments(
                            when (type) {
                                JAEMFileType.STORAGE -> AttachmentType.FILE
                                else -> AttachmentType.IMAGE_AND_VIDEO
                            },
                            attachmentPaths = bytes.mapNotNull { AppStorageHelper.copyUriToAppStorage(it, context)?.absolutePath }
                        )
                    )
                }
            }
        )
    }
}