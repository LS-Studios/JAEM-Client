package de.stubbe.jaem_client.repositories

import android.content.Context
import android.util.Log
import de.stubbe.jaem_client.database.entries.EncryptionKeyEntity
import de.stubbe.jaem_client.database.entries.MessageEntity
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.encryption.EncryptionContext
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.model.enums.ContentMessageType
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.model.network.OutgoingMessageDto
import de.stubbe.jaem_client.model.network.ReceivedMessageDto
import de.stubbe.jaem_client.model.network.SignatureRequestBodyDto
import de.stubbe.jaem_client.network.MessageDeliveryApiService
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.AppStorageHelper.createFileFromBytesInSharedStorage
import de.stubbe.jaem_client.utils.splitResponse
import de.stubbe.jaem_client.utils.toEd25519PublicKey
import de.stubbe.jaem_client.utils.toRSAPublicKey
import de.stubbe.jaem_client.utils.toX25519PublicKey
import kotlinx.coroutines.flow.first
import okhttp3.RequestBody
import javax.inject.Inject

class MessageDeliveryRepository @Inject constructor(
    val chatRepository: ChatRepository,
    val encryptionKeyRepository: EncryptionKeyRepository,
    val profileRepository: ProfileRepository,
    val messageRepository: MessageRepository,
    val messageDeliveryApiService: MessageDeliveryApiService,
    val userPreferencesRepository: UserPreferencesRepository
) {

    private val messageDeliveryUrlsFlow = userPreferencesRepository.messageDeliveryUrlsFlow

    private val deviceClientFlow = encryptionKeyRepository.getClientFlow()

    suspend fun initKeyExchange(sharedProfile: ShareProfileModel, serverUrl: ServerUrlModel?): Long {
        val deviceClient = deviceClientFlow.first()!!

        val newChatId = ShareProfileModel.addSharedProfileToDB(
            sharedProfile,
            profileRepository,
            encryptionKeyRepository,
            chatRepository,
            deviceClient
        )

        val deviceProfile = profileRepository.getProfileByUid(deviceClient.profileUid!!)!!
        val deviceSharedProfile = ShareProfileModel.fromProfileModel(deviceProfile, listOf(
            EncryptionKeyEntity(
                id = 0,
                key = deviceClient.ed25519PublicKey!!.encoded,
                type = KeyType.PUBLIC_ED25519,
                profileUid = deviceProfile.uid,
            ),
            EncryptionKeyEntity(
                id = 0,
                key = deviceClient.x25519PublicKey!!.encoded,
                type = KeyType.PUBLIC_X25519,
                profileUid = deviceProfile.uid,
            ),
            EncryptionKeyEntity(
                id = 0,
                key = deviceClient.rsaPublicKey!!.encoded,
                type = KeyType.PUBLIC_RSA,
                profileUid = deviceProfile.uid,
            )
        ))

        sendMessage(
            OutgoingMessageDto.createKeyExchange(
                EncryptionContext(
                    localClient = deviceClient,
                    remoteClient = ED25519Client(
                        profileUid = sharedProfile.uid,
                        ed25519PublicKey = sharedProfile.keys[0].key.toEd25519PublicKey(),
                        x25519PublicKey = sharedProfile.keys[1].key.toX25519PublicKey(),
                        rsaPublicKey = sharedProfile.keys[2].key.toRSAPublicKey(),
                    ),
                    encryptionAlgorithm = SymmetricEncryption.ED25519
                ),
                deviceSharedProfile.toByteArray()
            ),
            serverUrl
        )

        return newChatId
    }

    suspend fun receiveMessages(body: SignatureRequestBodyDto, deviceClient: ED25519Client, context: Context): List<MessageEntity> {

        val messages = mutableListOf<MessageEntity>()

        messageDeliveryUrlsFlow.first().forEach { serverUrl ->
            val (response, error) = messageDeliveryApiService.getMessages(
                "${serverUrl.url}/get_messages",
                RequestBody.create(null, body.toByteArray())
            ).splitResponse()

            if (error != null) {
                Log.e("NetworkRepository", "Error receiving messages from ${serverUrl.name}: ${error.string()}")
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

            messages.addAll(receivedMessages.mapNotNull { message ->
                if (message.messageType == MessageType.KEY_EXCHANGE) {
                    val exchangedProfile =
                        ShareProfileModel.fromByteArray(message.messagePartDtos.first().content)

                    ShareProfileModel.addSharedProfileToDB(
                        exchangedProfile,
                        profileRepository,
                        encryptionKeyRepository,
                        chatRepository,
                        deviceClient
                    )

                    Log.d(
                        "NetworkRepository",
                        "Successfully exchanged keys with profile: ${exchangedProfile.name}"
                    )

                    if (receivedMessages.size == 1) {
                        deleteMessage(serverUrl, body)
                    }

                    return@mapNotNull null
                }

                if (message.messageType == MessageType.DELETE_MESSAGE) {
                    val messageUid = String(message.messagePartDtos.first().content)

                    messageRepository.deleteMessageByUid(messageUid)

                    Log.d("NetworkRepository", "Successfully deleted message: $messageUid")

                    if (receivedMessages.size == 1) {
                        deleteMessage(serverUrl, body)
                    }

                    return@mapNotNull null
                }

                var chat = chatRepository.getChatByChatPartnerUid(message.senderUid)
                if (chat == null) {
                    chat = chatRepository.getChatByProfileUid(deviceClient.profileUid!!)
                }

                val messageUid =
                    String(message.messagePartDtos.find { it.type == ContentMessageType.UID }!!.content)

                val messageContent =
                    String(message.messagePartDtos.find { it.type == ContentMessageType.MESSAGE }!!.content)

                var attachments: Attachments? = null

                if (message.messagePartDtos.size > 2) {
                    val attachmentPaths = mutableListOf<String>()

                    message.messagePartDtos.forEach { messagePart ->
                        val newFile =
                            createFileFromBytesInSharedStorage(messagePart.content, context)
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
                    sendTime = message.timestamp,
                    deliveryTime = null
                )
            })

            if (messages.isNotEmpty()) {
                deleteMessage(serverUrl, body)
            }
        }

        Log.d("NetworkRepository", "Received messages: $messages")

        return messages
    }

    private suspend fun deleteMessage(serverUrl: ServerUrlModel, body: SignatureRequestBodyDto) {
        val (response, error) = messageDeliveryApiService.deleteMessage(
            "${serverUrl.url}/delete_messages",
            RequestBody.create(null, body.toByteArray())
        ).splitResponse()

        if (error == null) {
            Log.d("NetworkRepository", "Message deleted successfully from ${serverUrl.name}: $response")
        } else {
            Log.e("NetworkRepository", "Error deleting message from ${serverUrl.name}: ${error.string()}")
        }
    }

    suspend fun sendMessage(message: OutgoingMessageDto, url: ServerUrlModel?) {

        //TODO just send to server were uid is present

        val urlsToSendTo = (if (url != null) listOf(url) else messageDeliveryUrlsFlow.first())

        urlsToSendTo.forEach { serverUrl ->
            val (response, error) = messageDeliveryApiService.sendMessage(
                "${serverUrl.url}/send_message",
                RequestBody.create(null, message.toByteArray())
            ).splitResponse()

            if (error == null) {
                Log.d("NetworkRepository", "Message sent successfully to ${serverUrl.name}: $response")
            } else {
                Log.e("NetworkRepository", "Error sending message to ${serverUrl.name}: ${error.string()}")
            }
        }
    }

    suspend fun shareProfile(shareProfileModel: ShareProfileModel): List<Pair<ServerUrlModel, String?>> {
        val shareCodes = mutableListOf<Pair<ServerUrlModel, String?>>()

        messageDeliveryUrlsFlow.first().forEach { serverUrl ->
            val (response, error) = messageDeliveryApiService.share(
                "${serverUrl.url}/share",
                RequestBody.create(null, shareProfileModel.toByteArray())
            ).splitResponse()

            if (error == null) {
                Log.d("NetworkRepository", "Profile shared successfully on ${serverUrl.name}: $response")
                shareCodes.add(Pair(serverUrl, String(response!!.bytes())))
            } else {
                Log.e("NetworkRepository", "Error sharing profile on ${serverUrl.name}: ${error.string()}")
            }
        }

        return shareCodes
    }

    // UDS

    suspend fun getSharedProfile(shareLink: String): Pair<ServerUrlModel?, ShareProfileModel?> {

        var serverUrlFrom: ServerUrlModel? = null
        var sharedProfile: ShareProfileModel? = null

        messageDeliveryUrlsFlow.first().forEach { serverUrl ->
            val (response, error) = messageDeliveryApiService.getSharedProfile(
                "${serverUrl.url}/share/$shareLink"
            ).splitResponse()
            if (error == null) {
                Log.d("NetworkRepository", "Shared profile received successfully from ${serverUrl.name}: $response")
                sharedProfile = ShareProfileModel.fromByteArray(response!!.bytes())
                serverUrlFrom = serverUrl
                return@forEach
            } else {
                Log.e("NetworkRepository", "Error getting shared profile from ${serverUrl.name}: ${error.string()}")
            }
        }

        return serverUrlFrom to sharedProfile
    }

}