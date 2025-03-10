package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.database.entries.EncryptionKeyEntity
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.SharedProfileModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareProfileViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository
): ViewModel() {

    val sharedProfile: MutableStateFlow<SharedProfileModel?> = MutableStateFlow(null)

    val isShareProfileBottomSheetVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val profileToShare: MutableStateFlow<ProfilePresentationModel?> = MutableStateFlow(null)

    val noInternetConnection: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorCreatingSharedProfile: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val deviceClientFLow = encryptionKeyRepository.getClientFlow()

    fun openShareProfileBottomSheet(profile: ProfilePresentationModel) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchOrCreateShareProfileLink(profile)
            isShareProfileBottomSheetVisible.value = true
        }
    }

    fun closeShareProfileBottomSheet() {
        isShareProfileBottomSheetVisible.value = false
    }

    private fun fetchOrCreateShareProfileLink(profile: ProfilePresentationModel) {
        viewModelScope.launch {
            val deviceClient = deviceClientFLow.first() ?: return@launch

            profileToShare.value = profile

            val shareProfileModel = ShareProfileModel.fromProfilePresentationModel(
                profile,
                listOf(
                    EncryptionKeyEntity(
                        key = deviceClient.ed25519PublicKey!!.encoded,
                        type = KeyType.PUBLIC_ED25519,
                    ),
                    EncryptionKeyEntity(
                        key = deviceClient.x25519PublicKey!!.encoded,
                        type = KeyType.PUBLIC_X25519,
                    ),
                    EncryptionKeyEntity(
                        key = deviceClient.rsaPublicKey!!.encoded,
                        type = KeyType.PUBLIC_RSA,
                    )
                )
            )

            val sharedCodeCall = networkRepository.shareProfile(shareProfileModel)

            val sharedCode = when (sharedCodeCall.status) {
                NetworkCallStatusType.SUCCESS -> {
                    sharedCodeCall.response
                }
                NetworkCallStatusType.NO_INTERNET -> {
                    noInternetConnection.value = true
                    return@launch
                }
                NetworkCallStatusType.ERROR -> {
                    errorCreatingSharedProfile.value = true
                    return@launch
                }
            }

            sharedProfile.value = SharedProfileModel(
                sharedCode = sharedCode ?: "",
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun resetErrorFlags() {
        noInternetConnection.value = false
        errorCreatingSharedProfile.value = false
    }
}