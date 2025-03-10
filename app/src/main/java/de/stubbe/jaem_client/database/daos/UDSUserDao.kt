package de.stubbe.jaem_client.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import de.stubbe.jaem_client.database.entries.UDSUserEntity

@Dao
interface UDSUserDao {

    @Upsert
    suspend fun updateAll(udsUsers: List<UDSUserEntity>)

    @Query("SELECT * FROM uds_users WHERE username LIKE '%' || :query || '%' ORDER BY id ASC")
    fun pagingSource(query: String): PagingSource<Int, UDSUserEntity>

    @Query("DELETE FROM uds_users")
    suspend fun clearAll()

}