package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.ProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Profile Datenbank.
 */
@Dao
abstract class ProfileDao: BaseDao<ProfileEntity> {

    @Query("SELECT * FROM profiles WHERE uid = :uid")
    abstract suspend fun getProfileByUid(uid: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE uid = :uid")
    abstract fun getProfileByUidWithChange(uid: String): Flow<ProfileEntity>

    @Query("SELECT * FROM profiles")
    abstract fun getAllProfiles(): Flow<List<ProfileEntity>>

}