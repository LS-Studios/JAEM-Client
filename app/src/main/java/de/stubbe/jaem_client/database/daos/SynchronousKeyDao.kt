package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.SymmetricKeyModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Chat Datenbank.
 */
@Dao
abstract class SymmetricKeyDao: BaseDao<SymmetricKeyModel> {

    @Query("SELECT * FROM symmetric_keys WHERE id = :id")
    abstract fun getSymmetricKeyPairsById(id: Int): Flow<List<SymmetricKeyModel>>

    @Query("SELECT * FROM symmetric_keys WHERE device_id = :profileId")
    abstract fun getSymmetricKeyPairsByProfileId(profileId: Int): Flow<List<SymmetricKeyModel>>

}