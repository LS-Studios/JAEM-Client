package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class EditServerListViewModel @AssistedInject constructor(
    @Assisted val serverListType: ServerListType,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val networkRepository: NetworkRepository,
    private val profileRepository: ProfileRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository
): ViewModel() {

    companion object {
        fun provideFactory(factory: Factory, serverListType: ServerListType): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(serverListType) as T
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(serverListType: ServerListType): EditServerListViewModel
    }

    val urls: MutableStateFlow<List<ServerUrlModel>> = MutableStateFlow(emptyList())
    var editServerDialogIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var serverUrlToEdit: MutableStateFlow<ServerUrlModel?> = MutableStateFlow(null)

    init {
        userPreferencesRepository.userPreferencesFlow
            .map {
                when (serverListType) {
                    ServerListType.MESSAGE_DELIVERY -> it.messageDeliveryUrlsList
                    ServerListType.USER_DISCOVERY -> it.udsUrlsList
                }
            }
            .onEach { urls.value = it }
            .launchIn(viewModelScope)
    }

    fun addUrl(url: ServerUrlModel) {
        urls.value += url
    }

    fun editUrl(oldUrl: ServerUrlModel, newUrl: ServerUrlModel) {
        urls.value = urls.value.map { if (it == oldUrl) newUrl else it }
    }

    fun removeUrl(url: ServerUrlModel) {
        urls.value -= url
    }

    val clientFlow = encryptionKeyRepository.getClientFlow()

    suspend fun saveUrls(joinOrLeaveServers: Boolean): Pair<List<ServerUrlModel>, List<ServerUrlModel>> {
        return withContext(Dispatchers.IO) {
            val removedUrls = when (serverListType) {
                ServerListType.MESSAGE_DELIVERY -> userPreferencesRepository.messageDeliveryUrlsFlow.first()
                ServerListType.USER_DISCOVERY -> userPreferencesRepository.udsUrlsFlow.first()
            }.toSet() - urls.value.toSet()

            val addedUrls = urls.value.toSet() - when (serverListType) {
                ServerListType.MESSAGE_DELIVERY -> userPreferencesRepository.messageDeliveryUrlsFlow.first()
                ServerListType.USER_DISCOVERY -> userPreferencesRepository.udsUrlsFlow.first()
            }.toSet()

            if (joinOrLeaveServers) {
                val client = clientFlow.first()!!
                val profile = profileRepository.getProfileByUid(client.profileUid!!)!!

                removedUrls.forEach {
                    when (serverListType) {
                        ServerListType.USER_DISCOVERY -> networkRepository.leaveService(
                            it.url,
                            profile.uid
                        )

                        else -> {}
                    }
                }

                addedUrls.forEach {
                    when (serverListType) {
                        ServerListType.USER_DISCOVERY -> networkRepository.joinService(
                            it.url,
                            UDSUserDto.fromProfile(profile, client)
                        )

                        else -> {}
                    }
                }
            }

            when (serverListType) {
                ServerListType.MESSAGE_DELIVERY -> userPreferencesRepository.updateMessageDeliveryUrls(urls.value)
                ServerListType.USER_DISCOVERY -> userPreferencesRepository.updateUdsUrls(urls.value)
            }

            Pair(addedUrls.toList(), removedUrls.toList())
        }
    }

    fun openEditServerDialog(serverUrl: ServerUrlModel?) {
        serverUrlToEdit.value = serverUrl
        editServerDialogIsOpen.value = true
    }

    fun closeEditServerDialog() {
        editServerDialogIsOpen.value = false
    }

}