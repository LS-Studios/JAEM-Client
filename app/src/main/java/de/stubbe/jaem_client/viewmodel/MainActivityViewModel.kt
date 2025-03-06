package de.stubbe.jaem_client.viewmodel

import ED25519Client
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.ChatRequestRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
    private val messageRepository: MessageRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val chatRequestRepository: ChatRequestRepository,
    private val jaemApiService: JAEMApiService
): ViewModel() {

    val userPreferences = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = UserPreferences.getDefaultInstance()
        )

    fun updateTheme(theme: Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.updateTheme(theme)
        }
    }

    fun deleteExampleData(context: Context) {
        context.deleteDatabase("jaem_database")
    }

    fun printData() {
        viewModelScope.launch(Dispatchers.IO) {
            println("-------------- Printing data --------------")
            val profiles = profileRepository.getAllProfiles().first()
            val chats = chatRepository.getAllChats().first()
            val messages = messageRepository.getAllMessages().first()

            profiles.forEach { profile ->
                println("Profile: $profile")
            }

            chats.forEach { chat ->
                println("Chat: $chat")
            }

            messages.forEach { message ->
                println("Message: $message")
            }
        }
    }

    fun addExampleData() {
        fun fetchPicture(): ByteArray {
            val url = URL("https://picsum.photos/200")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connect()

            val inputStream: InputStream = connection.inputStream
            val byteArray = inputStream.readBytes()
            inputStream.close()

            return byteArray
        }

        viewModelScope.launch(Dispatchers.IO) {
            val profile = ProfileModel(
                id = 0,
                uid = "123",
                name = "Max Mustermann",
                profilePicture = fetchPicture(),
                description = "Ich bin ein Beispielprofil",
            )

            val profileId = profileRepository.insertProfile(profile).toInt()

            userPreferencesRepository.updateUserProfileId(profileId)

            val chatPartner = ProfileModel(
                id = 0,
                uid = "456",
                name = "Lisa Mustermann",
                profilePicture = fetchPicture(),
                description = "Ich bin ein anderes Beispielprofil",
            )

            val chatPartnerId = profileRepository.insertProfile(chatPartner).toInt()

            val chat1 = ChatModel(
                id = 0,
                profileId = profileId,
                chatPartnerId = chatPartnerId
            )

            chatRepository.insertChat(chat1)

            val chatPartnerClient = ED25519Client()

            encryptionKeyRepository.insertNewClient(chatPartnerClient, chatPartnerId)
        }
    }
}