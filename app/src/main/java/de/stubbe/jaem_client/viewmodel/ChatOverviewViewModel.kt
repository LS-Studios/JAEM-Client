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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatOverviewViewModel @Inject constructor(
    val chatRepository: ChatRepository,
    val messageRepository: MessageRepository,
    val profileRepository: ProfileRepository,
    val userPreferencesRepository: UserPreferencesRepository,
    val encryptionKeyRepository: EncryptionKeyRepository
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
            profilePicture = profile.profilePicture,
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

    suspend fun isDeviceInitialized(): Boolean {
        return withContext(Dispatchers.IO) {
            userPreferencesRepository.isInitializedFlow.first()
        }
    }
}