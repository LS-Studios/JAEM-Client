package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.entries.ChatPresentationModel
import de.stubbe.jaem_client.repositories.ChatRepository
import de.stubbe.jaem_client.repositories.MessageRepository
import de.stubbe.jaem_client.repositories.ProfileRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
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
    userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val chatFlow = chatRepository.getAllChats()
    private val messageFlow = messageRepository.getAllMessages()
    private val profileFlow = profileRepository.getAllProfiles()

    private val chatId: MutableStateFlow<Int> = MutableStateFlow(-1)

    val chat = combine(
        chatFlow, messageFlow, profileFlow
    ) { chats, messages, profiles ->
        val chat = chats.find { it.id == chatId.value } ?: return@combine null

        val chatPartner = profiles.find { it.id == chat.chatPartnerId } ?: return@combine null

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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    val messages = combine(messageFlow, chatId) { messages, chatId ->
        messages.filter { it.chatId == chatId }
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

    val newMessageString: MutableStateFlow<String> = MutableStateFlow("")
    val newAttachment: MutableStateFlow<File?> = MutableStateFlow(null)

    val searchValue: MutableStateFlow<String> = MutableStateFlow("")
    val foundItemIndices: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())
    val currentFoundItemIndex: MutableStateFlow<Int> = MutableStateFlow(-1)

    /**
     * Initialisiert den Chat
     *
     * @param chatId id des Chats dessen Daten geladen werden sollen
     */
    fun initializeChat(chatId: Int) {
        this.chatId.value = chatId
    }

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
                    chatId = chatId.value,
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