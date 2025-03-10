package de.stubbe.jaem_client.data.pagination

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.database.entries.UDSUserEntity
import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.repositories.NetworkRepository
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class UDSUserRemoteMediator(
    private val query: String,
    private val jaemDatabase: JAEMDatabase,
    private val networkRepository: NetworkRepository
): RemoteMediator<Int, UDSUserEntity>() {

    companion object {
        private const val INITIAL_LOAD_PAGE = 0
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UDSUserEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> INITIAL_LOAD_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        INITIAL_LOAD_PAGE
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val udsUsersCall = if (query.isEmpty()) networkRepository.getUsers(
                loadKey,
                state.config.pageSize
            ) else networkRepository.findUsersByUsername(
                query,
                loadKey,
                state.config.pageSize
            )

            val udsUsers = when (udsUsersCall.status) {
                NetworkCallStatusType.SUCCESS -> udsUsersCall.response!!
                NetworkCallStatusType.NO_INTERNET -> return MediatorResult.Error(Throwable("No internet connection"))
                NetworkCallStatusType.ERROR -> return MediatorResult.Error(Throwable("Network call failed"))
            }

            Log.d("UDSUserRemoteMediator", "loadType: $loadType, loadKey: $loadKey, udsUsers: ${udsUsers.size}")

            jaemDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    jaemDatabase.udsUserDao().clearAll()
                }
                val udsUserEntities = udsUsers.map { UDSUserEntity.fromDto(it) }
                jaemDatabase.udsUserDao().updateAll(udsUserEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = udsUsers.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

}