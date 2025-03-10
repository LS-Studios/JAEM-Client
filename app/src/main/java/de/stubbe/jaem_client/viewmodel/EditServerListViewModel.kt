package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    val urls: MutableStateFlow<List<String>> = MutableStateFlow(
        when (serverListType) {
            ServerListType.MESSAGE_DELIVERY -> runBlocking(Dispatchers.IO) { userPreferencesRepository.getMessageDeliveryUrlsFlow.first() }
            ServerListType.USER_DISCOVERY -> runBlocking(Dispatchers.IO) { userPreferencesRepository.getUdsUrlsFlow.first() }
        }
    )

    fun addUrl(url: String) {
        urls.value += url
    }

    fun removeUrl(url: String) {
        urls.value -= url
    }

    val clientFlow = encryptionKeyRepository.getClientFlow()

    fun saveUrls() {
        viewModelScope.launch {
            val deletedUrls = when (serverListType) {
                ServerListType.MESSAGE_DELIVERY -> userPreferencesRepository.getMessageDeliveryUrlsFlow.first()
                ServerListType.USER_DISCOVERY -> userPreferencesRepository.getUdsUrlsFlow.first()
            }.toSet() - urls.value.toSet()

            val addedUrls = urls.value.toSet() - when (serverListType) {
                ServerListType.MESSAGE_DELIVERY -> userPreferencesRepository.getMessageDeliveryUrlsFlow.first()
                ServerListType.USER_DISCOVERY -> userPreferencesRepository.getUdsUrlsFlow.first()
            }.toSet()

            val client = clientFlow.first()!!
            val profile = profileRepository.getProfileByUid(client.profileUid!!)!!

            deletedUrls.forEach {
                when (serverListType) {
                    ServerListType.USER_DISCOVERY -> networkRepository.leaveService(it, profile.uid)
                    else -> {}
                }
            }

            addedUrls.forEach {
                when (serverListType) {
                    ServerListType.USER_DISCOVERY -> networkRepository.joinService(it, UDSUserDto.fromProfile(profile, client))
                    else -> {}
                }
            }

            when (serverListType) {
                ServerListType.MESSAGE_DELIVERY -> userPreferencesRepository.updateMessageDeliveryUrls(urls.value)
                ServerListType.USER_DISCOVERY -> userPreferencesRepository.updateUdsUrls(urls.value)
            }
        }
    }

}