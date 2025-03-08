package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.model.network.EncryptionContext
import de.stubbe.jaem_client.model.network.MessagePart
import de.stubbe.jaem_client.model.network.OutgoingMessage
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val networkRepository: NetworkRepository,
    private val messageRepository: MessageRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {

    private val chatScreenArguments = savedStateHandle.toRoute<NavRoute.ChatMessages>()

    private val messageFlow = messageRepository.getAllMessages()
    private val deviceClientFlow = encryptionKeyRepository.getClientFlow()
    private val chatPartnerClientFlow = encryptionKeyRepository.getClientFlow(chatScreenArguments.profileUid)

    val messages = messageFlow
        .map{ messages ->
            messages.filter { it.chatId == chatScreenArguments.chatId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = emptyList()
        )

    val userProfileId = userPreferencesRepository.userPreferencesFlow
        .map { it.userProfileUid }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = null
        )

    val newMessageString: MutableStateFlow<String> = MutableStateFlow("")
    val newAttachments: MutableStateFlow<Attachments?> = MutableStateFlow(null)

    val searchValue: MutableStateFlow<String> = MutableStateFlow("")
    val foundItemIndices: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())
    val currentFoundItemIndex: MutableStateFlow<Int> = MutableStateFlow(-1)

    val selectedMessages: MutableStateFlow<List<MessageModel>> = MutableStateFlow(emptyList())

    val attachmentPickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val chatPartnerClient = chatPartnerClientFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = null
    )

    fun changeMessageString(newMessage: String) {
        newMessageString.value = newMessage
    }

    fun changeAttachment(newAttachments: Attachments?) {
        this.newAttachments.value?.attachmentPaths?.forEach { path ->
            File(path).delete()
        }
        this.newAttachments.value = newAttachments
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

    fun openAttachmentPicker() {
        attachmentPickerIsOpen.value = true
    }

    fun closeAttachmentPicker() {
        attachmentPickerIsOpen.value = false
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
    fun sendMessage(chat: ChatModel) {
        viewModelScope.launch(Dispatchers.IO) {
            /*messageRepository.insertMessage(
                MessageModel(
                    id = 0,
                    senderId = userProfileId.value ?: -1,
                    receiverId = chat.chatPartnerId,
                    chatId = chatScreenArguments.chatId,
                    stringContent = newMessageString.value,
                    attachments = newAttachments.value,
                    sendTime = System.currentTimeMillis(),
                    deliveryTime = null
                )
            )*/

            networkRepository.sendMessage(
               OutgoingMessage.create(
                   EncryptionContext(
                       localClient = deviceClientFlow.first(),
                       remoteClient = deviceClientFlow.first(),
                       encryptionAlgorithm = SymmetricEncryption.ED25519
                   ),
                   MessagePart.createMessageParts(
                       newMessageString.value,
                       newAttachments.value
                   )
               )
            )

            newMessageString.value = ""
            newAttachments.value = null
        }
    }

    /**
     * Löscht die ausgewählten Nachrichten
     */
    fun deleteSelectedMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedMessages.value.forEach { message ->
                messageRepository.deleteMessage(message)
            }
            selectedMessages.value = emptyList()
        }
    }
}