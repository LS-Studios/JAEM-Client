package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.repositories.ChatRepository
import de.stubbe.jaem_client.repositories.MessageRepository
import de.stubbe.jaem_client.repositories.ProfileRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.utils.toBitmap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ChatOverviewViewModel(
    chatRepository: ChatRepository,
    messageRepository: MessageRepository,
    profileRepository: ProfileRepository,
    userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val chatFlow = chatRepository.getAllChats()
    private val messageFlow = messageRepository.getAllMessages()
    private val profileFlow = profileRepository.getAllProfiles()

    val chats = combine(
        chatFlow, messageFlow, profileFlow
    ) { chats, messages, profiles ->
        chats.mapNotNull { chat ->
            val chatPartner = profiles.find { it.id == chat.chatPartnerId } ?: return@mapNotNull null

            val lastMessages = messages
                .filter { it.chatId == chat.id }
                .sortedBy { it.sendTime }
                .takeIf { it.isNotEmpty() }?.let { msgList ->
                    msgList.filter { it.deliveryTime == null }
                        .takeIf { it.isNotEmpty() } ?: msgList.takeLast(1)
                }.orEmpty()

            ChatPresentationModel(
                profilePicture = chatPartner.image?.toBitmap(),
                name = chatPartner.name,
                lastMessages = lastMessages,
                streak = 1,
                chat = chat
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val userProfileId = userPreferencesRepository.userPreferencesFlow
        .map { it.userProfileId }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

}