package de.stubbe.jaem_client.repositories

import android.content.Context
import android.util.Log
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.model.network.EncryptionContext
import de.stubbe.jaem_client.model.network.OutgoingMessage
import de.stubbe.jaem_client.model.network.ReceivedMessage
import de.stubbe.jaem_client.model.network.SignatureRequestBody
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.utils.AppStorageHelper.createFileFromBytesInSharedStorage
import de.stubbe.jaem_client.utils.splitResponse
import de.stubbe.jaem_client.utils.toEpochSeconds
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

    @Throws(Exception::class)
    suspend fun receiveMessages(body: SignatureRequestBody, deviceClient: ED25519Client, context: Context): List<MessageModel> {
        val (response, error) = jaemApiService.getMessages(RequestBody.create(null, body.toByteArray())).splitResponse()

        if (error != null) {
            throw Exception("Error receiving messages: ${error.string()}")
        }

        val byteMessages = ReceivedMessage.extractMessages(response!!.bytes())

        val receivedMessages = byteMessages.map { message ->
            val messageModel = ReceivedMessage.fromByteArray(
                message,
                deviceClient
            ) { profileUid ->
                EncryptionContext(
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

            val messageContent = String(message.messageParts.find { it.type == MessageType.MESSAGE }!!.content)

            var attachments: Attachments? = null

            if (message.messageParts.size > 1) {
                val attachmentPaths = mutableListOf<String>()

                message.messageParts.filter { it.type == MessageType.MESSAGE }.forEach { messagePart ->
                    val newFile = createFileFromBytesInSharedStorage(messagePart.content, context)
                    if (newFile != null) {
                        attachmentPaths.add(newFile.absolutePath)
                    }
                }

                val attachmentType = when (message.messageParts[2].type) {
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
                sendTime = message.timestamp.toLocalDateTime().toEpochSeconds(),
                deliveryTime = LocalDateTime.now().toEpochSeconds()
            )
        }

        if (messages.isNotEmpty()) {
            deleteMessage(body)
        }

        Log.d("NetworkRepository", "Received messages: $messages")

        return messages
    }

    suspend fun deleteMessage(body: SignatureRequestBody){
        val (response, error ) = jaemApiService.deleteMessage(RequestBody.create(null, body.toByteArray())).splitResponse()
        if (error == null) {
            Log.d("NetworkRepository", "Message deleted successfully: $response")
        } else {
            Log.e("NetworkRepository", "Error deleting message: ${error.string()}")
        }
    }

    suspend fun sendMessage(message: OutgoingMessage) {
        val (response, error) = jaemApiService.sendMessage(RequestBody.create(null, message.toByteArray())).splitResponse()
        if (error == null) {
            Log.d("NetworkRepository", "Message sent successfully: $response")
        } else {
            Log.e("NetworkRepository", "Error sending message: ${error.string()}")
        }
    }

    suspend fun shareProfile(shareProfileModel: ShareProfileModel): String? {
        val (response, error) = jaemApiService.share(RequestBody.create(null, shareProfileModel.toByteArray())).splitResponse()
        if (error == null) {
            Log.d("NetworkRepository", "Profile shared successfully: $response")
            return response!!.string()
        } else {
            Log.e("NetworkRepository", "Error sharing profile: ${error.string()}")
            return null
        }
    }

    suspend fun getSharedProfile(shareLink: String): ShareProfileModel? {
        val (response, error) = jaemApiService.getSharedProfile(shareLink).splitResponse()
        if (error == null) {
            Log.d("NetworkRepository", "Shared profile received successfully: $response")
            return ShareProfileModel.fromByteArray(response!!.bytes())
        } else {
            Log.e("NetworkRepository", "Error getting shared profile: ${error.string()}")
            return null
        }
    }

}