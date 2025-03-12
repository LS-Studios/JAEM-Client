package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val profileFlow = editProfileArguments.profileUid?.let { profileRepository.getProfileByUidFlow(it) }

    val createProfile = editProfileArguments.profileUid == null

    val noInternetConnection: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val creationError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val profileAlreadyExists: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val fetchingProfile: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val serverUrlOfSharedProfile: MutableStateFlow<ServerUrlModel?> = MutableStateFlow(null)
    private val sharedProfile: MutableStateFlow<ShareProfileModel?> = MutableStateFlow(null)

    val profilePicture: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val profileName: MutableStateFlow<String> = MutableStateFlow("")
    val profileDescription: MutableStateFlow<String> = MutableStateFlow("")

    val imagePickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val deviceClientFlow = encryptionKeyRepository.getClientFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchingProfile.value = true

            profileFlow?.onEach { profile ->
                profilePicture.value = profile?.profilePicture
                profileName.value = profile?.name ?: ""
                profileDescription.value = profile?.description ?: ""
            }?.launchIn(this)

            if (createProfile) {
                networkRepository.getSharedProfile(editProfileArguments.sharedCode!!).let { profileCall ->
                    val profile = when (profileCall.status) {
                        NetworkCallStatusType.SUCCESS -> profileCall.response?.second
                        NetworkCallStatusType.NO_INTERNET -> {
                            noInternetConnection.value = true
                            return@let
                        }
                        NetworkCallStatusType.ERROR -> {
                            creationError.value = true
                            return@let
                        }
                    }

                    if (profile == null) {
                        creationError.value = true
                        return@let
                    }

                    val existingProfile = profileRepository.getProfileByUid(profile.uid)

                    if (existingProfile != null) {
                        profileAlreadyExists.value = true
                        return@let
                    }

                    serverUrlOfSharedProfile.value = profileCall.response?.first
                    sharedProfile.value = profile

                    profilePicture.value = profile.profilePicture
                    profileName.value = profile.name
                    profileDescription.value = profile.description
                }
            }
            fetchingProfile.value = false
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

    fun updateProfile(navigationViewModel: NavigationViewModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!createProfile && profileFlow!!.first() != null) {
                profileRepository.updateProfile(
                    profileFlow.first()!!.copy(
                        profilePicture = profilePicture.value,
                        name = profileName.value,
                        description = profileDescription.value
                    )
                )
            } else if (sharedProfile.value != null){
                val keyExchangeCall = networkRepository.doKeyExchange(sharedProfile.value!!, serverUrlOfSharedProfile.value)

                if (keyExchangeCall.status == NetworkCallStatusType.SUCCESS) {
                    withContext(Dispatchers.Main) {
                        navigationViewModel.navigateTo(
                            NavRoute.ChatMessages(
                                sharedProfile.value!!.uid,
                                keyExchangeCall.response!!.toInt(),
                                false
                            )
                        )
                    }
                }
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