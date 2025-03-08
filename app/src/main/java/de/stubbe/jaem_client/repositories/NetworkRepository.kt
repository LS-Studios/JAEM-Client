package de.stubbe.jaem_client.repositories

import ED25519Client
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.model.network.AddPubKeyBody
import de.stubbe.jaem_client.model.network.PubKey
import de.stubbe.jaem_client.model.network.UDSModel
import de.stubbe.jaem_client.network.ChatEncryptionData
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.network.ReceiveBody
import de.stubbe.jaem_client.network.ReceivedMessagesModel
import de.stubbe.jaem_client.network.SendMessageModel
import de.stubbe.jaem_client.network.UDSApiService
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.utils.splitResponse
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.RequestBody
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val encryptionKeyRepository: EncryptionKeyRepository,
    val jaemApiService: JAEMApiService,
    val udsApiService: UDSApiService
) {
    suspend fun receiveMessages(body: ReceiveBody, deviceClient: ED25519Client): List<ReceivedMessagesModel> {
        val (response, error) = jaemApiService.getMessages(RequestBody.create(null, body.toByteArray())).splitResponse()

        if (error != null) {
            println("Error receiving messages: ${error.string()}")
            return emptyList()
        }

        val byteMessages = ReceivedMessagesModel.messageFromByteArray(response!!.bytes())

        val messages = byteMessages.map { message ->
            val messageModel = ReceivedMessagesModel.fromByteArray(
                message,
                deviceClient,
                { profileUid -> ChatEncryptionData(
                    deviceClient,
                    encryptionKeyRepository.getClientFlow(profileUid).first()!!,
                    SymmetricEncryption.ED25519
                )}
            )
            messageModel
        }

        messages.forEach { message ->

        }

        return messages
    }

    suspend fun deleteMessage(message: SendMessageModel){
        val (response, error ) = jaemApiService.deleteMessage(RequestBody.create(null, message.toByteArray())).splitResponse()
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

    suspend fun getUserProfile(uid: String): UDSModel {
        val (response, error) = udsApiService.getUserProfile(uid).splitResponse()
        if (error == null) {
            return Json.decodeFromString(UDSModel.serializer(), response!!.string())
        } else {
            println("Error getting user profile: ${error.string()}")
            return UDSModel("", "", emptyList(), "")
        }
    }

    suspend fun searchUserByUsername(username: String): UDSModel {
        val (response, error) = udsApiService.findUsersByUsername(username).splitResponse()
        if (error == null) {
            return Json.decodeFromString(UDSModel.serializer(), response!!.string())
        } else {
            println("Error searching user by username: ${error.string()}")
            return UDSModel("", "", emptyList(), "")
        }
    }

    suspend fun joinService(client: ED25519Client, username: String, profilePicture: String): String {
        val joinUDSRequestBody = UDSModel(
            client.profileUid!!,
            username,
            listOf(
                PubKey(
                    client.encryption,
                    String(client.ed25519PublicKey!!.encoded),
                    String(client.x25519PublicKey!!.encoded),
                    client.rsaPublicKey!!
                )
            ),
            profilePicture)
        val requestJson = String(Json.encodeToString(UDSModel.serializer(), joinUDSRequestBody).toByteArray())
        val (response, error) = udsApiService.joinService(requestJson).splitResponse()
        if (error == null) {
            return response!!.string()
        } else {
            println("Error joining service: ${error.string()}")
            return ""
        }
    }

    suspend fun leaveService(uid: String): String {
        val (response, error) = udsApiService.leaveService(uid).splitResponse()
        if (error == null) {
            return response!!.string()
        } else {
            println("Error leaving service: ${error.string()}")
            return ""
        }
    }

    suspend fun addPublicKey(uid: String, pubKey: PubKey): String {
        val addPubKeyBody = AddPubKeyBody(uid, pubKey)
        val (response, error) = udsApiService.addPubKey(Json.encodeToString(AddPubKeyBody.serializer(), addPubKeyBody)).splitResponse()
        if (error == null) {
            return response!!.string()
        } else {
            println("Error adding public key: ${error.string()}")
            return ""
        }

    }
}