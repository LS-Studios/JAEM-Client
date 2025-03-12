package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SharedChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    chatRepository: ChatRepository,
    profileRepository: ProfileRepository,
    messageRepository: MessageRepository,
    encryptionKeyRepository: EncryptionKeyRepository
) : ViewModel() {

    private val chatScreenArguments = savedStateHandle.toRoute<NavRoute.ChatMessages>()

    private val chatFlow = chatRepository.getAllChats()
    private val messageFlow = messageRepository.getAllMessages()
    private val profilesFlow = profileRepository.getAllProfiles()
    private val profileFlow = profileRepository.getProfileByUidFlow(chatScreenArguments.profileUid)
    private val clientFlow = encryptionKeyRepository.getClientFlow(chatScreenArguments.profileUid)

    val chat = combine(
        chatFlow, messageFlow, profilesFlow
    ) { chats, messages, profiles ->
        val chat = chats.find { it.id == chatScreenArguments.chatId } ?: return@combine null

        val chatPartner = profiles.find { it.uid == chat.chatPartnerUid } ?: return@combine null

        val chatMessages = messages.filter { it.chatId == chat.id }

        val unreadMessages = chatMessages
            .filter { it.deliveryTime == null && it.senderUid == chatPartner.uid }
            .sortedBy { it.sendTime }

        val lastMessage = chatMessages
            .maxByOrNull { it.sendTime }

        ChatPresentationModel(
            profilePicture = chatPartner.profilePicture,
            name = chatPartner.name,
            lastMessage = lastMessage,
            unreadMessages = unreadMessages,
            streak = ChatPresentationModel.calculateStreak(chatMessages),
            chat = chat
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = null
    )

    val profile = combine(
        profileFlow, clientFlow
    ) { profile, client ->
        if (client == null || profile == null) return@combine null

        ProfilePresentationModel(
            name = profile.name,
            profilePicture = profile.profilePicture,
            description = profile.description,
            client = client,
            profile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = null
    )

}