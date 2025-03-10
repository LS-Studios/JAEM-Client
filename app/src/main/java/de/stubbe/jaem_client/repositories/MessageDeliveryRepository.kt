package de.stubbe.jaem_client.repositories

import android.content.Context
import android.util.Log
import de.stubbe.jaem_client.database.entries.MessageEntity
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.encryption.EncryptionContext
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.model.enums.ContentMessageType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.model.network.OutgoingMessageDto
import de.stubbe.jaem_client.model.network.ReceivedMessageDto
import de.stubbe.jaem_client.model.network.SignatureRequestBodyDto
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.AppStorageHelper.createFileFromBytesInSharedStorage
import de.stubbe.jaem_client.utils.splitResponse
import de.stubbe.jaem_client.utils.toEpochSeconds
import de.stubbe.jaem_client.utils.toLocalDateTime
import kotlinx.coroutines.flow.first
import okhttp3.RequestBody
import javax.inject.Inject

class MessageDeliveryRepository @Inject constructor(
    val chatRepository: ChatRepository,
    val encryptionKeyRepository: EncryptionKeyRepository,
    val profileRepository: ProfileRepository,
    val messageRepository: MessageRepository,
    val jaemApiService: JAEMApiService,
) {

    @Throws(Exception::class)
    suspend fun receiveMessages(body: SignatureRequestBodyDto, deviceClient: ED25519Client, context: Context): List<MessageEntity> {
        val (response, error) = jaemApiService.getMessages(RequestBody.create(null, body.toByteArray())).splitResponse()

        if (error != null) {
            throw Exception("Error receiving messages: ${error.string()}")
        }

        val byteMessages = ReceivedMessageDto.extractMessageBytes(response!!.bytes())

        val receivedMessages = byteMessages.map { message ->
            val messageModel = ReceivedMessageDto.fromByteArray(
                message,
                deviceClient
            ) { profileUid ->
                EncryptionContext(
                    deviceClient,
                    encryptionKeyRepository.getClientFlow(profileUid).first(),
                    SymmetricEncryption.ED25519
                )
            }
            messageModel
        }

        val messages = receivedMessages.map { message ->
            if (message.messageType == MessageType.KEY_EXCHANGE) {
                    val exchangedProfile = ShareProfileModel.fromByteArray(message.messagePartDtos.first().content)

                    ShareProfileModel.addSharedProfileToDB(
                        exchangedProfile,
                        profileRepository,
                        encryptionKeyRepository,
                        chatRepository,
                        deviceClient
                    )

                    if (receivedMessages.size == 1) {
                        deleteMessage(body)
                    }

                    Log.d("NetworkRepository", "Successfully exchanged keys with profile: ${exchangedProfile.name}")
            }

            if (message.messageType == MessageType.DELETE_MESSAGE) {
                val messageUid = String(message.messagePartDtos.first().content)

                messageRepository.deleteMessageByUid(messageUid)

                if (receivedMessages.size == 1) {
                    deleteMessage(body)
                }

                Log.d("NetworkRepository", "Successfully deleted message: $messageUid")
            }

            var chat = chatRepository.getChatByChatPartnerUid(message.senderUid)
            if (chat == null) {
                chat = chatRepository.getChatByProfileUid(deviceClient.profileUid!!)
            }

            val messageUid =  String(message.messagePartDtos.find { it.type == ContentMessageType.UID }!!.content)

            val messageContent = String(message.messagePartDtos.find { it.type == ContentMessageType.MESSAGE }!!.content)

            var attachments: Attachments? = null

            if (message.messagePartDtos.size > 2) {
                val attachmentPaths = mutableListOf<String>()

                message.messagePartDtos.forEach { messagePart ->
                    val newFile = createFileFromBytesInSharedStorage(messagePart.content, context)
                    if (newFile != null) {
                        attachmentPaths.add(newFile.absolutePath)
                    }
                }

                val attachmentType = when (message.messagePartDtos[2].type) {
                    ContentMessageType.FILE -> AttachmentType.FILE
                    ContentMessageType.IMAGE_AND_VIDEO -> AttachmentType.IMAGE_AND_VIDEO
                    else -> AttachmentType.FILE
                }

                attachments = Attachments(attachmentType, attachmentPaths)
            }

            MessageEntity(
                id = 0,
                uid = messageUid,
                senderUid = message.senderUid,
                receiverUid = deviceClient.profileUid!!,
                chatId = chat!!.id,
                stringContent = messageContent,
                attachments = attachments,
                sendTime = message.timestamp.toLocalDateTime().toEpochSeconds(),
                deliveryTime = null
            )
        }

        if (messages.isNotEmpty()) {
            deleteMessage(body)
        }

        Log.d("NetworkRepository", "Received messages: $messages")

        return messages
    }

    suspend fun deleteMessage(body: SignatureRequestBodyDto) {
        val (response, error ) = jaemApiService.deleteMessage(RequestBody.create(null, body.toByteArray())).splitResponse()
        if (error == null) {
            Log.d("NetworkRepository", "Message deleted successfully: $response")
        } else {
            Log.e("NetworkRepository", "Error deleting message: ${error.string()}")
        }
    }

    suspend fun sendMessage(message: OutgoingMessageDto) {
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

    // UDS

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