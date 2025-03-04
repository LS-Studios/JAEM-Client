package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.AsymmetricKeyPairRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.ChatRequestRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.repositories.database.SymmetricKeyRepository
import de.stubbe.jaem_client.utils.toBitmap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatOverviewViewModel @Inject constructor(
    chatRepository: ChatRepository,
    messageRepository: MessageRepository,
    profileRepository: ProfileRepository,
    userPreferencesRepository: UserPreferencesRepository,
    asymmetricKeyPairRepository: AsymmetricKeyPairRepository,
    symmetricKeyRepository: SymmetricKeyRepository,
    chatRequestRepository: ChatRequestRepository
): ViewModel() {

    private val chatFlow = chatRepository.getAllChats()
    private val profileFlow = profileRepository.getAllProfiles()
    private val messageFlow = messageRepository.getAllMessages()
    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    private val chatRequestRepositoryFlow = chatRequestRepository.getAllChatRequests()

    val chatRequests = chatRequestRepositoryFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = emptyList()
    )

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
                profilePicture = chatPartner.profilePicture?.toBitmap(),
                name = chatPartner.name,
                lastMessages = lastMessages,
                streak = 1,
                chat = chat
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = emptyList()
    )

    val userProfile = userPreferencesFlow
        .map { userPreferences ->
            val profile = profileRepository.getProfileById(userPreferences.userProfileId) ?: return@map null

            val asymmetricKeyPairs =
                asymmetricKeyPairRepository.getAsymmetricKeyPairsByProfileId(userPreferences.userProfileId)
                    .first()
            val symmetricKeyPairs =
                symmetricKeyRepository.getSymmetricKeyPairsByProfileId(userPreferences.userProfileId)
                    .first()

            ProfilePresentationModel(
                name = profile.name,
                profilePicture = profile.profilePicture?.toBitmap(),
                description = profile.description,
                asymmetricKeyPairs = asymmetricKeyPairs,
                symmetricKeys = symmetricKeyPairs,
                profile = profile
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = null
        )

    val userProfileId = userPreferencesRepository.userPreferencesFlow
        .map { it.userProfileId }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = null
        )

}