package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class UDSViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    val chatRepository: ChatRepository,
    val encryptionKeyRepository: EncryptionKeyRepository
): ViewModel() {

    val searchText = MutableStateFlow("")

    val udsUserInfo: MutableStateFlow<ShareProfileModel?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val udsUsersPagingFlow = searchText.debounce(300.milliseconds).flatMapLatest { query ->
        networkRepository.getUDSUserPager(query)
            .flow
            .map { pagingData -> pagingData.map { UDSUserDto.fromEntity(it) } }
            .cachedIn(viewModelScope)
    }

    fun changeSearchText(text: String) {
        searchText.value = text
    }

    fun openUserInfoDialog(udsUserDto: UDSUserDto) {
        udsUserInfo.value = ShareProfileModel.fromUDSUserDto(udsUserDto)
    }

    fun closeUserInfoDialog() {
        udsUserInfo.value = null
    }

    fun startChatWithProfile(
        profile: ShareProfileModel,
        navigationViewModel: NavigationViewModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingChat = chatRepository.getChatByProfileUid(profile.uid)

            if (existingChat != null) {
                withContext(Dispatchers.Main) {
                    navigationViewModel.navigateTo(
                        NavRoute.ChatMessages(
                            profile.uid,
                            existingChat.id,
                            false
                        )
                    )
                }
            } else {
                val keyExchangeCall = networkRepository.doKeyExchange(profile, null)

                if (keyExchangeCall.status == NetworkCallStatusType.SUCCESS) {
                    withContext(Dispatchers.Main) {
                        navigationViewModel.navigateTo(
                            NavRoute.ChatMessages(
                                profile.uid,
                                keyExchangeCall.response!!.toInt(),
                                false
                            )
                        )
                    }
                }
            }
        }
    }

}