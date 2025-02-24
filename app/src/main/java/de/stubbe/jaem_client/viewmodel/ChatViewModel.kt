package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(
    private val messageRepository: MessageRepository,
    chatRepository: ChatRepository,
    profileRepository: ProfileRepository,
    userPreferencesRepository: UserPreferencesRepository,
    val chatId: Int
): ViewModel() {

    companion object {
        val CHAT_ID_KEY = object : CreationExtras.Key<Int> {}
    }

    private val chatFlow = chatRepository.getAllChats()
    private val messageFlow = messageRepository.getAllMessages()
    private val profileFlow = profileRepository.getAllProfiles()

    val chat = combine(
        chatFlow, messageFlow, profileFlow
    ) { chats, messages, profiles ->
        val chat = chats.find { it.id == chatId } ?: return@combine null

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

    val messages = messageFlow
        .map{ messages ->
            messages.filter { it.chatId == chatId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = emptyList()
        )

    val userProfileId = userPreferencesRepository.userPreferencesFlow
        .map { it.userProfileId }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = null
        )

    val newMessageString: MutableStateFlow<String> = MutableStateFlow("")
    val newAttachment: MutableStateFlow<File?> = MutableStateFlow(null)

    val searchValue: MutableStateFlow<String> = MutableStateFlow("")
    val foundItemIndices: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())
    val currentFoundItemIndex: MutableStateFlow<Int> = MutableStateFlow(-1)

    val selectedMessages: MutableStateFlow<List<MessageModel>> = MutableStateFlow(emptyList())

    fun changeMessageString(newMessage: String) {
        newMessageString.value = newMessage
    }

    fun changeAttachment(newAttachment: File) {
        this.newAttachment.value = newAttachment
    }

    fun changeSearchValue(newSearchValue: String) {
        searchValue.value = newSearchValue
    }

    fun changeFoundItemIndices(newFoundItemIndices: List<Int>) {
        foundItemIndices.value = newFoundItemIndices
    }

    fun changeCurrentFoundItemIndex(newCurrentFoundItemIndex: Int) {
        currentFoundItemIndex.value = newCurrentFoundItemIndex
    }

    fun changeSelectedMessages(newSelectedMessages: List<MessageModel>) {
        selectedMessages.value = newSelectedMessages
    }

    /**
     * Markiert neue Nachrichten als zugestellt
     */
    fun markNewMessageAsDelivered() {
        viewModelScope.launch(Dispatchers.IO) {
            val newMessages = messages.value.filter { it.deliveryTime == null }
            newMessages.forEach { message ->
                messageRepository.updateMessage(
                    message.copy(
                        deliveryTime = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    /**
     * Sendet die Nachricht
     */
    fun sendMessage() {
        if (chat.value == null) return

        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.insertMessage(
                MessageModel(
                    id = 0,
                    senderId = userProfileId.value ?: -1,
                    receiverId = chat.value!!.chat.chatPartnerId,
                    chatId = chatId,
                    stringContent = newMessageString.value,
                    filePath = newAttachment.value?.absolutePath,
                    sendTime = System.currentTimeMillis(),
                    deliveryTime = null
                )
            )

            newMessageString.value = ""
            newAttachment.value = null
        }
    }
}