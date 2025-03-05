package de.stubbe.jaem_client.repositories

import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.network.ReceiveBody
import de.stubbe.jaem_client.model.network.ShareProfileResponse
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.network.NetworkMessageModel
import de.stubbe.jaem_client.utils.executeSafely
import de.stubbe.jaem_client.utils.splitResponseIntoMessages
import de.stubbe.jaem_client.utils.toByteArray
import kotlinx.serialization.json.Json
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.await
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val retrofitInstance: JAEMApiService
) {
    suspend fun receiveMessages(body: ReceiveBody): List<NetworkMessageModel> {
        val (response, error) = retrofitInstance.getMessages(RequestBody.create(null, body.toByteArray())).executeSafely()
        if (error != null) {
            throw error
        }

        val messages = splitResponseIntoMessages(response!!.bytes())
        val messageModels: MutableList<NetworkMessageModel> = mutableListOf()

        for (message in messages) {
            val messageModel = (Json.decodeFromString(String(message)) as NetworkMessageModel)
            messageModels.add(messageModel)
        }
        return messageModels
    }
}


