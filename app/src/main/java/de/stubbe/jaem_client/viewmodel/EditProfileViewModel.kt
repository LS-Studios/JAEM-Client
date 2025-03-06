package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
): ViewModel() {

    private val editProfileArguments = savedStateHandle.toRoute<NavRoute.EditProfile>()

    private val profileFlow = profileRepository.getProfileByUidWithChange(editProfileArguments.profileUid)

    val profilePicture: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val profileName: MutableStateFlow<String> = MutableStateFlow("")
    val profileDescription: MutableStateFlow<String> = MutableStateFlow("")

    val imagePickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            profileFlow
                .onEach { profile ->
                    profilePicture.value = profile.profilePicture
                    profileName.value = profile.name
                    profileDescription.value = profile.description
                }
                .launchIn(this)
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
            profileRepository.updateProfile(
                profileFlow.first().copy(
                    profilePicture = profilePicture.value,
                    name = profileName.value,
                    description = profileDescription.value
                )
            )
        }
    }

    fun openImagePicker() {
        imagePickerIsOpen.value = true
    }

    fun closeImagePicker() {
        imagePickerIsOpen.value = false
    }

}