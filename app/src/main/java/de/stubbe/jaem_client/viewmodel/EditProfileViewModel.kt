package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val networkRepository: NetworkRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val chatRepository: ChatRepository
): ViewModel() {

    private val editProfileArguments = savedStateHandle.toRoute<NavRoute.EditProfile>()

    private val profileFlow = editProfileArguments.profileUid?.let { profileRepository.getProfileByUidWithChange(it) }

    val createProfile = editProfileArguments.profileUid == null
    val creationError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val sharedProfile: MutableStateFlow<ShareProfileModel?> = MutableStateFlow(null)

    val profilePicture: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val profileName: MutableStateFlow<String> = MutableStateFlow("")
    val profileDescription: MutableStateFlow<String> = MutableStateFlow("")

    val imagePickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            profileFlow?.onEach { profile ->
                profilePicture.value = profile.profilePicture
                profileName.value = profile.name
                profileDescription.value = profile.description
            }?.launchIn(this)

            if (createProfile) {
                networkRepository.getSharedProfile(editProfileArguments.sharedCode!!).let { profile ->
                    if (profile == null) {
                        creationError.value = true
                        return@let
                    }

                    sharedProfile.value = profile

                    profilePicture.value = profile.profilePicture
                    profileName.value = profile.name
                    profileDescription.value = profile.description
                }
            }
        }
    }

    suspend fun changeProfilePicture(picture: ByteArray) {
        profilePicture.value = picture
    }

    fun changeProfileName(name: String) {
        profileName.value = name
    }

    fun changeProfileDescription(description: String) {
        profileDescription.value = description
    }

    fun updateProfile() {
        viewModelScope.launch {
            if (createProfile) {
                profileRepository.updateProfile(
                    profileFlow!!.first().copy(
                        profilePicture = profilePicture.value,
                        name = profileName.value,
                        description = profileDescription.value
                    )
                )
            } else if (sharedProfile.value != null){
                val newProfile = ProfileModel(
                    id = 0,
                    uid = UUID.randomUUID().toString(),
                    profilePicture = profilePicture.value,
                    name = profileName.value,
                    description = profileDescription.value
                )

                profileRepository.insertProfile(newProfile)

                encryptionKeyRepository.insertAllEncryptionKeys(
                    sharedProfile.value!!.keys
                )

                chatRepository.insertChat(
                    ChatModel(
                        id = 0,
                        profileUid = newProfile.uid,
                        chatPartnerUid = sharedProfile.value!!.uid,
                    )
                )
            }
        }
    }

    fun openImagePicker() {
        imagePickerIsOpen.value = true
    }

    fun closeImagePicker() {
        imagePickerIsOpen.value = false
    }

}