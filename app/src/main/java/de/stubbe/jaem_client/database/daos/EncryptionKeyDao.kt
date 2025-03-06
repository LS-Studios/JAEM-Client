package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.EncryptionKeyModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Chat Datenbank.
 */
@Dao
abstract class EncryptionKeyDao: BaseDao<EncryptionKeyModel> {

    @Query("SELECT * FROM encryption_keys WHERE profile_uid = :profileUid")
    abstract fun getKeysFromProfileFlow(profileUid: String): Flow<List<EncryptionKeyModel>>

    @Query("SELECT * FROM encryption_keys WHERE profile_uid = :profileUid")
    abstract suspend fun getKeysFromProfile(profileUid: Int): List<EncryptionKeyModel>

}