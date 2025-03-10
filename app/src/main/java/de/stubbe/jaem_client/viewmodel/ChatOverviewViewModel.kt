package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.toBitmap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatOverviewViewModel @Inject constructor(
    chatRepository: ChatRepository,
    messageRepository: MessageRepository,
    profileRepository: ProfileRepository,
    userPreferencesRepository: UserPreferencesRepository,
    encryptionKeyRepository: EncryptionKeyRepository
): ViewModel() {

    private val chatFlow = chatRepository.getAllChats()
    private val profileFlow = profileRepository.getAllProfiles()
    private val messageFlow = messageRepository.getAllMessages()
    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    private val deviceClientFlow = encryptionKeyRepository.getClientFlow()

    val chats = combine(
        chatFlow, messageFlow, profileFlow
    ) { chats, messages, profiles ->
        chats.mapNotNull { chat ->
            val chatPartner = profiles.find { it.uid == chat.chatPartnerUid } ?: return@mapNotNull null

            val unreadMessages = messages
                .filter { it.chatId == chat.id && it.deliveryTime == null && it.senderUid == chatPartner.uid }
                .sortedBy { it.sendTime }

            ChatPresentationModel(
                profilePicture = chatPartner.profilePicture?.toBitmap(),
                name = chatPartner.name,
                lastMessage = messages.lastOrNull(),
                unreadMessages = unreadMessages,
                streak = 1,
                chat = chat
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = emptyList()
    )

    val userProfile = combine(
        userPreferencesFlow, deviceClientFlow
    ) { userPreferences, deviceClient ->
        val profile = profileRepository.getProfileByUid(userPreferences.userProfileUid) ?: return@combine null

        if (deviceClient == null) return@combine null

        ProfilePresentationModel(
            name = profile.name,
            profilePicture = profile.profilePicture?.toBitmap(),
            description = profile.description,
            client = deviceClient,
            profile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = null
    )

    val userProfileUid = userPreferencesRepository.userPreferencesFlow
        .map { it.userProfileUid }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = null
        )

}