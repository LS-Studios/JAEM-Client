package de.stubbe.jaem_client.repositories

import de.stubbe.jaem_client.model.network.ReceiveBody
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.network.NetworkMessageModel
import de.stubbe.jaem_client.network.SendNetworkMessageModel
import de.stubbe.jaem_client.network.toByteArray
import de.stubbe.jaem_client.utils.ChatEncryptionData
import de.stubbe.jaem_client.utils.executeSafely
import de.stubbe.jaem_client.utils.toByteArray
import de.stubbe.jaem_client.utils.toNetworkMessageModel
import okhttp3.RequestBody

import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val retrofitInstance: JAEMApiService,
    val encryptionData: ChatEncryptionData
) {
    suspend fun receiveMessages(body: ReceiveBody): NetworkMessageModel {
        val (response, error) = retrofitInstance.getMessages(RequestBody.create(null, body.toByteArray())).executeSafely()
        if (error != null) {
            throw error
        }

        val messages = response!!.bytes().toNetworkMessageModel(encryptionData)

        return messages
    }

    suspend fun sendMessage(message: SendNetworkMessageModel): Throwable {
        val (response, error) = retrofitInstance.sendMessage(RequestBody.create(null, message.toByteArray())).executeSafely()
        if (error != null) {
            return error
        }

        return Throwable()
    }
}


