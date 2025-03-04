package de.stubbe.jaem_client.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.database.entries.AsymmetricKeyPairModel
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.ChatRequestModel
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.database.entries.SymmetricKeyModel
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.AsymmetricKeyPairRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.ChatRequestRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.repositories.database.SymmetricKeyRepository
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
    private val asymmetricKeyPairRepository: AsymmetricKeyPairRepository,
    private val symmetricKeyRepository: SymmetricKeyRepository,
    private val chatRequestRepository: ChatRequestRepository
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
            val chatRequest1 = ChatRequestModel(
                id = 0,
                publicKey = "1234567890123456",
                profileId = 10,
                chatPartnerId = 20
            )

            chatRequestRepository.insertChatRequest(chatRequest1)

            val profile1 = ProfileModel(
                id = 0,
                uid = "123",
                name = "Max Mustermann",
                profilePicture = fetchPicture(),
                description = "Ich bin ein Beispielprofil",
            )

            val newProfile1Id = profileRepository.insertProfile(profile1).toInt()

            userPreferencesRepository.updateUserProfileId(newProfile1Id)

            val profile2 = ProfileModel(
                id = 0,
                uid = "456",
                name = "Lisa Mustermann",
                profilePicture = fetchPicture(),
                description = "Ich bin ein anderes Beispielprofil",
            )

            val newProfile2Id = profileRepository.insertProfile(profile2).toInt()

            val profile3 = ProfileModel(
                id = 0,
                uid = "789",
                name = "Ulrich Mustermann",
                profilePicture = fetchPicture(),
                description = "Ich bin ein noch anderes Beispielprofil",
            )

            val newProfile3Id = profileRepository.insertProfile(profile3).toInt()

            val chat1 = ChatModel(
                id = 0,
                profileId = newProfile1Id,
                chatPartnerId = newProfile2Id
            )

            val newChat1Id = chatRepository.insertChat(chat1).toInt()

            val message1 = MessageModel(
                id = 0,
                senderId = newProfile2Id,
                receiverId = newProfile1Id,
                chatId = newChat1Id,
                stringContent = "Hallo!",
                attachments = null,
                sendTime = System.currentTimeMillis(),
                deliveryTime = null
            )

            val newMessage1Id = messageRepository.insertMessage(message1).toInt()

            val chat2 = ChatModel(
                id = 0,
                profileId = newProfile1Id,
                chatPartnerId = newProfile3Id
            )

            val newChat2Id = chatRepository.insertChat(chat2).toInt()

            val message2 = MessageModel(
                id = 0,
                senderId = newProfile3Id,
                receiverId = newProfile1Id,
                chatId = newChat2Id,
                stringContent = "Ne oder?",
                attachments = null,
                sendTime = System.currentTimeMillis(),
                deliveryTime = null
            )

            val newMessage2Id = messageRepository.insertMessage(message2).toInt()

            val message3 = MessageModel(
                id = 0,
                senderId = newProfile3Id,
                receiverId = newProfile1Id,
                chatId = newChat2Id,
                stringContent = "Du bist echt durch Junge \uD83D\uDE02!",
                attachments = null,
                sendTime = System.currentTimeMillis(),
                deliveryTime = null
            )

            val newMessage3Id = messageRepository.insertMessage(message3).toInt()

            val symmetricKey = SymmetricKeyModel(
                id = 0,
                key = "1234567890123456",
                profileId = newProfile2Id,
                encryption = SymmetricEncryption.AES
            )

            symmetricKeyRepository.insertSymmetricKeyPair(symmetricKey)

            val asymmetricKeyPair1 = AsymmetricKeyPairModel(
                id = 0,
                name = "Schlüssel 1",
                publicKey = "1234567890123456",
                privateKey = "1234567890123456",
                profileId = newProfile2Id,
                encryption = AsymmetricEncryption.ED25519
            )

            asymmetricKeyPairRepository.insertAsymmetricKeyPair(asymmetricKeyPair1)

            val asymmetricKeyPair2 = AsymmetricKeyPairModel(
                id = 0,
                name = "Schlüssel 2",
                publicKey = "fdhjsfijfhksdjhfjkdshfkjdsf",
                privateKey = "fdhjsfijfhksdjhfjkdshfkjdsf",
                profileId = newProfile2Id,
                encryption = AsymmetricEncryption.ED25519
            )

            asymmetricKeyPairRepository.insertAsymmetricKeyPair(asymmetricKeyPair2)

            val asymmetricKeyPair3 = AsymmetricKeyPairModel(
                id = 0,
                name = "Schlüssel 3",
                publicKey = "fdhjsfijfhksdjhfjkdshfkjdsf",
                privateKey = "fdhjsfijfhksdjhfjkdshfkjdsf",
                profileId = newProfile2Id,
                encryption = AsymmetricEncryption.ED25519
            )

            asymmetricKeyPairRepository.insertAsymmetricKeyPair(asymmetricKeyPair3)
        }
    }
}