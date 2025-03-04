package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.ProfileModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Profile Datenbank.
 */
@Dao
abstract class ProfileDao: BaseDao<ProfileModel> {

    @Query("SELECT * FROM profiles WHERE id = :id")
    abstract suspend fun getProfileById(id: Int): ProfileModel?

    @Query("SELECT * FROM profiles WHERE id = :id")
    abstract fun getProfileByIdWithChange(id: Int): Flow<ProfileModel>

    @Query("SELECT * FROM profiles")
    abstract fun getAllProfiles(): Flow<List<ProfileModel>>

}