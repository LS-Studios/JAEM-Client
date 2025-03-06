package de.stubbe.jaem_client.repositories

import ED25519Client
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.network.ChatEncryptionData
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.network.ReceiveBody
import de.stubbe.jaem_client.network.ReceivedMessagesModel
import de.stubbe.jaem_client.network.SendMessageModel
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.utils.splitResponse
import kotlinx.coroutines.flow.first
import okhttp3.RequestBody
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val encryptionKeyRepository: EncryptionKeyRepository,
    val jaemApiService: JAEMApiService
) {
    suspend fun receiveMessages(body: ReceiveBody, deviceClient: ED25519Client): List<ReceivedMessagesModel> {
        val (response, error) = jaemApiService.getMessages(RequestBody.create(null, body.toByteArray())).splitResponse()

        if (error != null) {
            println("Error receiving messages: ${error.string()}")
            return emptyList()
        }

        println("Received messages: ${response}")

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

        return messages
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