package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.database.entries.EncryptionKeyModel
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.model.network.EncryptionContext
import de.stubbe.jaem_client.model.network.OutgoingMessage
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.toEd25519PublicKey
import de.stubbe.jaem_client.utils.toRSAPublicKey
import de.stubbe.jaem_client.utils.toX25519PublicKey
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
    private val networkRepository: NetworkRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val chatRepository: ChatRepository
): ViewModel() {

    private val editProfileArguments = savedStateHandle.toRoute<NavRoute.EditProfile>()

    private val profileFlow = editProfileArguments.profileUid?.let { profileRepository.getProfileByUidWithChange(it) }

    val createProfile = editProfileArguments.profileUid == null

    val creationError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val profileAlreadyExists: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val fetchingProfile: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val sharedProfile: MutableStateFlow<ShareProfileModel?> = MutableStateFlow(null)

    val profilePicture: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val profileName: MutableStateFlow<String> = MutableStateFlow("")
    val profileDescription: MutableStateFlow<String> = MutableStateFlow("")

    val imagePickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val deviceClientFlow = encryptionKeyRepository.getClientFlow()

    init {
        viewModelScope.launch {
            fetchingProfile.value = true

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

                    val existingProfile = profileRepository.getProfileByUid(profile.uid)

                    if (existingProfile != null) {
                        profileAlreadyExists.value = true
                        return@let
                    }

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

    fun updateProfile() {
        viewModelScope.launch {
            if (!createProfile) {
                profileRepository.updateProfile(
                    profileFlow!!.first().copy(
                        profilePicture = profilePicture.value,
                        name = profileName.value,
                        description = profileDescription.value
                    )
                )
            } else if (sharedProfile.value != null){
                val deviceClient = deviceClientFlow.first()!!

                sharedProfile.value!!.let { sharedProfile ->
                    ShareProfileModel.addSharedProfileToDB(
                        sharedProfile,
                        profileRepository,
                        encryptionKeyRepository,
                        chatRepository,
                        deviceClient
                    )

                    val deviceProfile = profileRepository.getProfileByUid(deviceClient.profileUid!!)!!
                    val deviceSharedProfile = ShareProfileModel.fromProfileModel(deviceProfile, listOf(
                        EncryptionKeyModel(
                            id = 0,
                            key = deviceClient.ed25519PublicKey!!.encoded,
                            type = KeyType.PUBLIC_ED25519,
                            profileUid = deviceProfile.uid,
                        ),
                        EncryptionKeyModel(
                            id = 0,
                            key = deviceClient.x25519PublicKey!!.encoded,
                            type = KeyType.PUBLIC_X25519,
                            profileUid = deviceProfile.uid,
                        ),
                        EncryptionKeyModel(
                            id = 0,
                            key = deviceClient.rsaPublicKey!!.encoded,
                            type = KeyType.PUBLIC_RSA,
                            profileUid = deviceProfile.uid,
                        )
                    ))

                    networkRepository.sendMessage(
                        OutgoingMessage.createKeyExchange(
                            EncryptionContext(
                                localClient = deviceClient,
                                remoteClient = ED25519Client(
                                    profileUid = sharedProfile.uid,
                                    ed25519PublicKey = sharedProfile.keys[0].key.toEd25519PublicKey(),
                                    x25519PublicKey = sharedProfile.keys[1].key.toX25519PublicKey(),
                                    rsaPublicKey = sharedProfile.keys[2].key.toRSAPublicKey(),
                                ),
                                encryptionAlgorithm = SymmetricEncryption.ED25519
                            ),
                            deviceSharedProfile.toByteArray()
                        )
                    )
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