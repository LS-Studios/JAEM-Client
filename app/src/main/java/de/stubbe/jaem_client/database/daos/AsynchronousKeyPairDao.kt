package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.AsymmetricKeyPairModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Chat Datenbank.
 */
@Dao
abstract class AsymmetricKeyPairDao: BaseDao<AsymmetricKeyPairModel> {

    @Query("SELECT * FROM asymmetric_key_pairs WHERE id = :id")
    abstract fun getAsymmetricKeyPairsById(id: Int): Flow<List<AsymmetricKeyPairModel>>

    @Query("SELECT * FROM asymmetric_key_pairs WHERE profile_id = :profileId")
    abstract fun getAsymmetricKeyPairsByProfileId(profileId: Int): Flow<List<AsymmetricKeyPairModel>>

}