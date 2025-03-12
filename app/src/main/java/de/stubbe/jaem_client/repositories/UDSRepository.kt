package de.stubbe.jaem_client.repositories

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.stubbe.jaem_client.data.pagination.UDSUserRemoteMediator
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.network.UDSApiService
import de.stubbe.jaem_client.utils.splitResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UDSRepository @Inject constructor(
    private val jaemDatabase: JAEMDatabase,
    private val udsApiService: UDSApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    private val udsUrlsFlow = userPreferencesRepository.udsUrlsFlow


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

        udsUrlsFlow.first().forEach { serverUrl ->
            val result = udsApiService.getUserProfile("${serverUrl.url}/user_by_uid/$uid")
            if (result.isSuccessful) {
                results.add(result.body()!!)
            }
        }

        Log.d("UDSRepository", "User profile fetched successfully: $results")

        return results.first()
    }

    suspend fun getUsers(page: Int, pageSize: Int): List<UDSUserDto> {
        val results = mutableListOf<UDSUserDto>()

        udsUrlsFlow.first().forEach { serverUrl ->
            val result = udsApiService.getUsers("${serverUrl.url}/users/$page/$pageSize")
            results.addAll(result)
        }

        Log.d("UDSRepository", "Users fetched successfully: $results")

        return results
    }

    suspend fun findUsersByUsername(username: String, page: Int, pageSize: Int): List<UDSUserDto> {
        val results = mutableListOf<UDSUserDto>()

        udsUrlsFlow.first().forEach { serverUrl ->
            val result = udsApiService.findUsersByUsername("${serverUrl.name}/search_users/$username/$page/$pageSize")
            results.addAll(result)
        }

        Log.d("UDSRepository", "Users found successfully: $results")

        return results
    }

    suspend fun joinService(url: String, udsUserDto: UDSUserDto): String {
        val (response, error) = udsApiService.joinService("$url/create_user", udsUserDto).splitResponse()

        if (error == null) {
            Log.d("NetworkRepository", "Service joined successfully")
        } else {
            Log.e("NetworkRepository", "Error while joining service ${url}: ${error.string()}")
        }


        return String(response!!.bytes())
    }

    suspend fun leaveService(url: String, uid: String): Boolean {
        val (_, error) = udsApiService.leaveService("$url/user/$uid").splitResponse()

        if (error == null) {
            Log.d("NetworkRepository", "Service left successfully")
            return true
        } else {
            Log.e("NetworkRepository", "Error while leaving service ${url}: ${error.string()}")
            return false
        }
    }

}