package de.stubbe.jaem_client.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.database.entries.ChatEntity
import de.stubbe.jaem_client.database.entries.ProfileEntity
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.datastore.UserPreferences.Language
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.changeAppLanguage
import de.stubbe.jaem_client.utils.fetchPicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InitDeviceViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val chatRepository: ChatRepository,
    private val networkRepository: NetworkRepository
): ViewModel() {

    val profilePicture: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val name: MutableStateFlow<String> = MutableStateFlow("")
    val description: MutableStateFlow<String> = MutableStateFlow("")
    val allowProfileSharing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val initializingProfile: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val createdProfileUid: MutableStateFlow<String> = MutableStateFlow("")
    private val clientFlow = encryptionKeyRepository.getClientFlow()

    val addedUrls: MutableStateFlow<List<ServerUrlModel>> = MutableStateFlow(emptyList())
    val removedUrls: MutableStateFlow<List<ServerUrlModel>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Creating profile
            initDevice()

            // Generate keys
            coroutineScope {
                launch(Dispatchers.Default) {
                    val client = ED25519Client(createdProfileUid.value)
                    encryptionKeyRepository.insertNewClient(client, createdProfileUid.value)
                }
            }

            initializingProfile.value = false
        }
    }

    private suspend fun initDevice() {
        val picture = fetchPicture()

        val uid = UUID.randomUUID().toString()

        val profile = ProfileEntity(
            id = 0,
            uid = uid,
            name = "",
            profilePicture = picture,
            description = "",
            allowProfileSharing = true
        )

        profileRepository.insertProfile(profile)

        profilePicture.value = profile.profilePicture
        name.value = profile.name
        description.value = profile.description
        allowProfileSharing.value = profile.allowProfileSharing

        userPreferencesRepository.updateUserProfileUid(profile.uid)

        createdProfileUid.value = uid
    }

    fun completeInitialization() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = profileRepository.getProfileByUid(createdProfileUid.value)!!

            profileRepository.updateProfile(
                profile.copy(
                    name = name.value,
                    profilePicture = profilePicture.value!!,
                    description = description.value,
                    allowProfileSharing = allowProfileSharing.value
                )
            )

            // Create initial chat with you self
            val chat = ChatEntity(
                id = 0,
                profileUid = createdProfileUid.value,
                chatPartnerUid = createdProfileUid.value
            )

            chatRepository.insertChat(chat)

            userPreferencesRepository.updateIsInitialized(true)

            // Join or leave servers
            val client = clientFlow.first()!!

            removedUrls.value.forEach {
                networkRepository.leaveService(
                    it.url,
                    profile.uid
                )
            }

            addedUrls.value.forEach {
                networkRepository.joinService(
                    it.url,
                    UDSUserDto.fromProfile(profile, client)
                )
            }
        }
    }

    val isMessageDeliveryDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUdsDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val imagePickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun changeProfilePicture(profilePicture: ByteArray) {
        this.profilePicture.value = profilePicture
    }

    fun changeName(name: String) {
        this.name.value = name
    }

    fun changeDescription(description: String) {
        this.description.value = description
    }

    fun changeAllowProfileSharing(allowProfileSharing: Boolean) {
        this.allowProfileSharing.value = allowProfileSharing
    }

    fun openMessageDeliveryDialog() {
        isMessageDeliveryDialogOpen.value = true
    }

    fun closeMessageDeliveryDialog() {
        isMessageDeliveryDialogOpen.value = false
    }

    fun openUdsDialog() {
        isUdsDialogOpen.value = true
    }

    fun closeUdsDialog() {
        isUdsDialogOpen.value = false
    }

    fun openImagePicker() {
        imagePickerIsOpen.value = true
    }

    fun closeImagePicker() {
        imagePickerIsOpen.value = false
    }

    fun changeTheme(theme: Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.updateTheme(theme)
        }
    }

    fun changeLanguage(context: Context, language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            changeAppLanguage(context, language)
            userPreferencesRepository.updateLanguage(language)
        }
    }

    fun setAddedAndRemovedUrls(addedUrls: List<ServerUrlModel>, removedUrls: List<ServerUrlModel>) {
        this.addedUrls.value = addedUrls
        this.removedUrls.value = removedUrls
    }

}