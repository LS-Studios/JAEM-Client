package de.stubbe.jaem_client.repositories

import android.content.Context
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.network.ChatEncryptionData
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.network.ReceiveBody
import de.stubbe.jaem_client.network.ReceivedMessagesModel
import de.stubbe.jaem_client.network.SendMessageModel
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.utils.AppStorageHelper.createFileFromBytesInSharedStorage
import de.stubbe.jaem_client.utils.splitResponse
import de.stubbe.jaem_client.utils.toEpochMillis
import de.stubbe.jaem_client.utils.toLocalDateTime
import kotlinx.coroutines.flow.first
import okhttp3.RequestBody
import java.time.LocalDateTime
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val chatRepository: ChatRepository,
    val encryptionKeyRepository: EncryptionKeyRepository,
    val jaemApiService: JAEMApiService
) {
    suspend fun receiveMessages(body: ReceiveBody, deviceClient: ED25519Client, context: Context): List<MessageModel> {
        val (response, error) = jaemApiService.getMessages(RequestBody.create(null, body.toByteArray())).splitResponse()

        if (error != null) {
            println("Error receiving messages: ${error.string()}")
            return emptyList()
        }

        val byteMessages = ReceivedMessagesModel.messageFromByteArray(response!!.bytes())

        val receivedMessages = byteMessages.map { message ->
            val messageModel = ReceivedMessagesModel.fromByteArray(
                message,
                deviceClient
            ) { profileUid ->
                ChatEncryptionData(
                    deviceClient,
                    encryptionKeyRepository.getClientFlow(profileUid).first()!!,
                    SymmetricEncryption.ED25519
                )
            }
            messageModel
        }

        val messages = receivedMessages.map { message ->
            var chat = chatRepository.getChatByChatPartnerUid(message.senderUid)
            if (chat == null) {
                chat = chatRepository.getChatByProfileUid(deviceClient.profileUid!!)
            }

            val messageContent = String(message.messages.find { it.messageType == MessageType.MESSAGE }!!.message)

            var attachments: Attachments? = null

            if (message.messages.size > 1) {
                val attachmentPaths = mutableListOf<String>()

                message.messages.filter { it.messageType == MessageType.MESSAGE }.forEach { messagePart ->
                    val newFile = createFileFromBytesInSharedStorage(messagePart.message, context)
                    if (newFile != null) {
                        attachmentPaths.add(newFile.absolutePath)
                    }
                }

                val attachmentType = when (message.messages[2].messageType) {
                    MessageType.FILE -> AttachmentType.FILE
                    MessageType.IMAGE_AND_VIDEO -> AttachmentType.IMAGE_AND_VIDEO
                    MessageType.MESSAGE -> AttachmentType.FILE
                }

                attachments = Attachments(attachmentType, attachmentPaths)
            }

            MessageModel(
                id = 0,
                senderUid = message.senderUid,
                receiverUid = deviceClient.profileUid!!,
                chatId = chat!!.id,
                stringContent = messageContent,
                attachments = attachments,
                sendTime = message.timeSend.toLocalDateTime().toEpochMillis(),
                deliveryTime = LocalDateTime.now().toEpochMillis()
            )
        }

        deleteMessage(body)

        return messages
    }

    suspend fun deleteMessage(body: ReceiveBody){
        val (response, error ) = jaemApiService.deleteMessage(RequestBody.create(null, body.toByteArray())).splitResponse()
        if (error == null) {
            println("Message deleted successfully: $response")
        } else {
            val errorBody = error.string().orEmpty()
            println("Error deleting message: $errorBody")
        }
    }

    suspend fun sendMessage(message: SendMessageModel) {
        val (response, error) = jaemApiService.sendMessage(RequestBody.create(null, message.toByteArray())).splitResponse()
        if (error == null) {
            println("Message sent successfully: $response")
        } else {
            val errorBody = error.string().orEmpty()
            println("Error sending message: $errorBody")
        }
    }
}