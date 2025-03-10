package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.repositories.NetworkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class UDSViewModel @Inject constructor(
    val networkRepository: NetworkRepository
): ViewModel() {

    val searchText = MutableStateFlow("")

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

}