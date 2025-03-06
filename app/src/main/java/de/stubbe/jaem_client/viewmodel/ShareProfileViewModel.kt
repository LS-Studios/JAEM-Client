package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.database.entries.EncryptionKeyModel
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.model.network.ShareProfileResponse
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

    val shareProfileResponse: MutableStateFlow<ShareProfileResponse?> = MutableStateFlow(null)

    val isShareProfileBottomSheetVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val profileToShare: MutableStateFlow<ProfilePresentationModel?> = MutableStateFlow(null)

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

    private suspend fun fetchOrCreateShareProfileLink(profile: ProfilePresentationModel) {
        viewModelScope.launch {
            val deviceClient = deviceClientFLow.first() ?: return@launch

            profileToShare.value = profile

            val shareProfileModel = ShareProfileModel.fromProfilePresentationModel(
                profile,
                listOf(
                    EncryptionKeyModel(
                        key = deviceClient.ed25519PublicKey!!.encoded,
                        type = KeyType.PUBLIC_ED25519,
                    ),
                    EncryptionKeyModel(
                        key = deviceClient.x25519PublicKey!!.encoded,
                        type = KeyType.PUBLIC_X25519,
                    ),
                    EncryptionKeyModel(
                        key = deviceClient.rsaPublicKey!!.encoded,
                        type = KeyType.PUBLIC_RSA,
                    )
                )
            )

         /*   val (shareProfile, shareProfileError) = networkRepository.getSharedProfile(profile.profile.uid).executeSafely()

            if (shareProfile != null && shareProfileError == null) {
                shareProfileResponse.value = shareProfile
            } else {
                val (newShareProfile, newShareProfileError) = networkRepository.createShareProfile(shareProfileModel).executeSafely()

                if (newShareProfile != null && newShareProfileError == null) {
                    shareProfileResponse.value = newShareProfile
                }
            }*/
        }
    }
}