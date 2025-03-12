package de.stubbe.jaem_client.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.stubbe.jaem_client.database.entries.MessageEntity
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.network.NetworkCallStatus
import de.stubbe.jaem_client.model.network.OutgoingMessageDto
import de.stubbe.jaem_client.model.network.SignatureRequestBodyDto
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.network.observeConnectivityAsFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageDeliveryRepository: MessageDeliveryRepository,
    private val udsRepository: UDSRepository
) {
    private val connectionStateFlow = context.observeConnectivityAsFlow()

    // Message Delivery

    suspend fun doKeyExchange(sharedProfile: ShareProfileModel, serverUrl: ServerUrlModel?): NetworkCallStatus<Long> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            messageDeliveryRepository.initKeyExchange(sharedProfile, serverUrl)
        }
    }

    suspend fun receiveMessages(body: SignatureRequestBodyDto, deviceClient: ED25519Client, context: Context): NetworkCallStatus<List<MessageEntity>> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            messageDeliveryRepository.receiveMessages(body, deviceClient, context)
        }
    }

    suspend fun sendMessage(message: OutgoingMessageDto, url: ServerUrlModel? = null): NetworkCallStatus<Unit> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            messageDeliveryRepository.sendMessage(message, url)
        }
    }

    suspend fun shareProfile(shareProfileModel: ShareProfileModel): NetworkCallStatus<List<Pair<ServerUrlModel, String?>>> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            messageDeliveryRepository.shareProfile(shareProfileModel)
        }
    }

    suspend fun getSharedProfile(shareLink: String): NetworkCallStatus<Pair<ServerUrlModel?, ShareProfileModel?>> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            messageDeliveryRepository.getSharedProfile(shareLink)
        }
    }

    // UDS

    fun getUDSUserPager(query: String) = udsRepository.getUDSUserPager(query, this)

    suspend fun getUserProfile(uid: String): NetworkCallStatus<UDSUserDto> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            udsRepository.getUserProfile(uid)
        }
    }

    suspend fun getUsers(page: Int, pageSize: Int): NetworkCallStatus<List<UDSUserDto>> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            udsRepository.getUsers(page, pageSize)
        }
    }

    suspend fun findUsersByUsername(username: String, page: Int, pageSize: Int): NetworkCallStatus<List<UDSUserDto>> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            udsRepository.findUsersByUsername(username, page, pageSize)
        }
    }

    suspend fun joinService(url: String, udsUserDto: UDSUserDto): NetworkCallStatus<String> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            udsRepository.joinService(url, udsUserDto)
        }
    }

    suspend fun leaveService(url: String, profileUid: String): NetworkCallStatus<Unit> {
        val connectionState = connectionStateFlow.first()

        return NetworkCallStatus.create(connectionState) {
            udsRepository.leaveService(url, profileUid)
        }
    }

}