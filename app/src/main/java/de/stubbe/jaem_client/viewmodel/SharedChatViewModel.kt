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
import de.stubbe.jaem_client.repositories.database.AsymmetricKeyPairRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.repositories.database.SymmetricKeyRepository
import de.stubbe.jaem_client.utils.toBitmap
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
    asymmetricKeyPairRepository: AsymmetricKeyPairRepository,
    symmetricKeyRepository: SymmetricKeyRepository
) : ViewModel() {

    private val chatScreenArguments = savedStateHandle.toRoute<NavRoute.ChatMessages>()

    private val chatFlow = chatRepository.getAllChats()
    private val messageFlow = messageRepository.getAllMessages()
    private val profilesFlow = profileRepository.getAllProfiles()
    private val profileFlow = profileRepository.getProfileByIdWithChange(chatScreenArguments.profileId)
    private val asymmetricKeyPairsFlow = asymmetricKeyPairRepository.getAsymmetricKeyPairsByProfileId(chatScreenArguments.profileId)
    private val symmetricKeyPairsFlow = symmetricKeyRepository.getSymmetricKeyPairsByProfileId(chatScreenArguments.profileId)

    val chat = combine(
        chatFlow, messageFlow, profilesFlow
    ) { chats, messages, profiles ->
        val chat = chats.find { it.id == chatScreenArguments.chatId } ?: return@combine null

        val chatPartner = profiles.find { it.id == chat.chatPartnerId } ?: return@combine null

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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = null
    )

    val profile = combine(
        profileFlow, asymmetricKeyPairsFlow, symmetricKeyPairsFlow
    ) { profile, asymmetricKeyPairs, symmetricKeyPairs ->
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

}