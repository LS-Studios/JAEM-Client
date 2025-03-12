package de.stubbe.jaem_client.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.database.entries.ChatEntity
import de.stubbe.jaem_client.database.entries.ProfileEntity
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.model.network.SignatureRequestBodyDto
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.fetchPicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
    private val messageRepository: MessageRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val networkRepository: NetworkRepository
): ViewModel() {

    val userPreferences = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = UserPreferences.getDefaultInstance()
        )

    private val deviceClient = encryptionKeyRepository.getClientFlow()

    val noInternetConnection: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val connectionError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun getNewMessages(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!userPreferences.value.getIsInitialized()) return@launch

            val deviceClient = deviceClient.first()!!

            try {
                val messagesCall = networkRepository.receiveMessages(
                    SignatureRequestBodyDto(deviceClient),
                    deviceClient,
                    context
                )

                val messages = when (messagesCall.status) {
                    NetworkCallStatusType.SUCCESS -> {
                        messagesCall.response!!
                    }
                    NetworkCallStatusType.NO_INTERNET -> {
                        noInternetConnection.value = true
                        return@launch
                    }
                    NetworkCallStatusType.ERROR -> {
                        Log.e("MainActivityViewModel", "Error while receiving messages", messagesCall.exception!!)
                        connectionError.value = true
                        return@launch
                    }
                }

                if (messages.isNotEmpty()) {
                    messageRepository.insertMessages(messages)
                }
            } catch (e: Exception) {
                println(e.printStackTrace())
            }
        }
    }

    fun deleteExampleData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataStoreFile = File(context.filesDir, "datastore/user_preferences")
            if (dataStoreFile.exists()) {
                dataStoreFile.delete()
            }
            context.deleteDatabase("jaem_database")
        }
    }

    fun addExampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = ProfileEntity(
                id = 0,
                uid = UUID.randomUUID().toString(),
                name = "Lisa Mustermann",
                profilePicture = fetchPicture(),
                description = "Ich bin ein Beispielprofil",
                allowProfileSharing = true
            )

            profileRepository.insertProfile(profile)

            userPreferencesRepository.updateUserProfileUid(profile.uid)

            val chat = ChatEntity(
                id = 0,
                profileUid = profile.uid,
                chatPartnerUid = profile.uid,
            )

            chatRepository.insertChat(chat)
        }
    }
}