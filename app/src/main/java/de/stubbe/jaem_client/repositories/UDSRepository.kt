package de.stubbe.jaem_client.repositories

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.stubbe.jaem_client.data.pagination.UDSUserRemoteMediator
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.network.UDSApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UDSRepository @Inject constructor(
    private val jaemDatabase: JAEMDatabase,
    private val udsApiService: UDSApiService,
    userPreferencesRepository: UserPreferencesRepository
) {

    private val udsUrlsFlow = userPreferencesRepository.getUdsUrlsFlow


    @OptIn(ExperimentalPagingApi::class)
    fun getUDSUserPager(
        query: String,
        networkRepository: NetworkRepository
    ) = Pager(
            config = PagingConfig(
                pageSize = 14,
                enablePlaceholders = false
            ),
            remoteMediator = UDSUserRemoteMediator(
                query = query,
                jaemDatabase = jaemDatabase,
                networkRepository = networkRepository
            ),
            pagingSourceFactory = {
                jaemDatabase.udsUserDao().pagingSource(query)
            }
    )

    suspend fun getUserProfile(uid: String): UDSUserDto {
        val results = mutableListOf<UDSUserDto>()

        udsUrlsFlow.first().forEach { url ->
            val result = udsApiService.getUserProfile("$url/user_by_uid/$uid")
            if (result.isSuccessful) {
                results.add(result.body()!!)
            }
        }

        return results.first()
    }

    suspend fun getUsers(page: Int, pageSize: Int): List<UDSUserDto> {
        val results = mutableListOf<UDSUserDto>()

        udsUrlsFlow.first().forEach { url ->
            val result = udsApiService.getUsers("$url/users/$page/$pageSize")
            results.addAll(result)
        }

        return results
    }

    suspend fun findUsersByUsername(username: String, page: Int, pageSize: Int): List<UDSUserDto> {
        val results = mutableListOf<UDSUserDto>()

        udsUrlsFlow.first().forEach { url ->
            val result = udsApiService.findUsersByUsername("$url/search_users/$username/$page/$pageSize")
            results.addAll(result)
        }

        return results
    }

    suspend fun joinService(url: String, udsUserDto: UDSUserDto): String {
        val result = udsApiService.joinService("$url/create_user", udsUserDto)

        Log.d("NetworkRepository", "Service joined successfully")

        return String(result.body()!!.bytes())
    }

    suspend fun leaveService(url: String, uid: String): Boolean {
        val result = udsApiService.leaveService("$url/user/$uid")

        Log.d("NetworkRepository", "Service left successfully")

        return result.isSuccessful
    }

}