package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARE_LINK_EXPIRATION_TIME
import de.stubbe.jaem_client.database.entries.EncryptionKeyEntity
import de.stubbe.jaem_client.datastore.CachedShareLinkModel
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.SharedProfileModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.utils.getUnixTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareProfileViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val sharedProfiles: MutableStateFlow<List<SharedProfileModel>?> = MutableStateFlow(null)

    val isShareProfileBottomSheetVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

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
        viewModelScope.launch(Dispatchers.IO) {
            val deviceClient = deviceClientFLow.first() ?: return@launch

            val cachedShareLinks = userPreferencesRepository.cachedShareLinks.first().filter { it.profileUid == profile.profile.uid }

            // Check validity of cached share links
            val validCachedShareLinks = cachedShareLinks.mapNotNull {
                if (it.timestamp + SHARE_LINK_EXPIRATION_TIME > getUnixTime()) {
                    it
                } else {
                    userPreferencesRepository.updateCachedShareLinks(cachedShareLinks.filter { link -> link != it })
                    null
                }
            }

            if (validCachedShareLinks.isNotEmpty()) {
                sharedProfiles.value = validCachedShareLinks.map { cachedShareLink ->
                    SharedProfileModel(
                        serverUrl = cachedShareLink.serverUrl,
                        sharedCode = cachedShareLink.sharedCode,
                        timestamp = cachedShareLink.timestamp
                    )
                }
                return@launch
            }

            val shareProfileModel = ShareProfileModel.fromProfilePresentationModel(
                profile,
                listOf(
                    EncryptionKeyEntity(
                        key = deviceClient.ed25519PublicKey!!.encoded,
                        type = KeyType.PUBLIC_ED25519,
                        profileUid = profile.profile.uid
                    ),
                    EncryptionKeyEntity(
                        key = deviceClient.x25519PublicKey!!.encoded,
                        type = KeyType.PUBLIC_X25519,
                        profileUid = profile.profile.uid
                    ),
                    EncryptionKeyEntity(
                        key = deviceClient.rsaPublicKey!!.encoded,
                        type = KeyType.PUBLIC_RSA,
                        profileUid = profile.profile.uid
                    )
                )
            )

            val sharedCodeCall = networkRepository.shareProfile(shareProfileModel)

            val sharedCodes = when (sharedCodeCall.status) {
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

            sharedProfiles.value = sharedCodes?.map { sharedCode ->
                SharedProfileModel(
                    serverUrl = sharedCode.first,
                    sharedCode = sharedCode.second ?: "",
                    timestamp = getUnixTime()
                )
            }

            userPreferencesRepository.updateCachedShareLinks(
                sharedCodes?.map { sharedCode ->
                    CachedShareLinkModel.newBuilder().apply {
                        setProfileUid(profile.profile.uid)
                        setServerUrl(sharedCode.first)
                        setSharedCode(sharedCode.second ?: "")
                        setTimestamp(getUnixTime())
                    }.build()
                } ?: emptyList()
            )
        }
    }

    fun resetErrorFlags() {
        noInternetConnection.value = false
        errorCreatingSharedProfile.value = false
    }
}