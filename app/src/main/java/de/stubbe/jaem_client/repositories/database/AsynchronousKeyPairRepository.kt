package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.AsymmetricKeyPairDao
import de.stubbe.jaem_client.database.entries.AsymmetricKeyPairModel

class AsymmetricKeyPairRepository(private val asymmetricKeyPairDao: AsymmetricKeyPairDao) {

    fun getAsymmetricKeyPairsById(id: Int) = asymmetricKeyPairDao.getAsymmetricKeyPairsById(id)

    fun getAsymmetricKeyPairsByProfileId(profileId: Int) = asymmetricKeyPairDao.getAsymmetricKeyPairsByProfileId(profileId)

    suspend fun insertAsymmetricKeyPair(asymmetricKeyPair: AsymmetricKeyPairModel) = asymmetricKeyPairDao.insert(asymmetricKeyPair)

    suspend fun updateAsymmetricKeyPair(asymmetricKeyPair: AsymmetricKeyPairModel) = asymmetricKeyPairDao.update(asymmetricKeyPair)

    suspend fun deleteAsymmetricKeyPair(asymmetricKeyPair: AsymmetricKeyPairModel) = asymmetricKeyPairDao.delete(asymmetricKeyPair)

}