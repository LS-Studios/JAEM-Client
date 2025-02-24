package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.SymmetricKeyDao
import de.stubbe.jaem_client.database.entries.SymmetricKeyModel

class SymmetricKeyRepository(private val symmetricKeyDao: SymmetricKeyDao) {

    fun getSymmetricKeyPairsById(id: Int) = symmetricKeyDao.getSymmetricKeyPairsById(id)

    fun getSymmetricKeyPairsByProfileId(profileId: Int) = symmetricKeyDao.getSymmetricKeyPairsByProfileId(profileId)

    suspend fun insertSymmetricKeyPair(symmetricKeyPair: SymmetricKeyModel) = symmetricKeyDao.insert(symmetricKeyPair)

    suspend fun updateSymmetricKeyPair(symmetricKeyPair: SymmetricKeyModel) = symmetricKeyDao.update(symmetricKeyPair)

    suspend fun deleteSymmetricKeyPair(symmetricKeyPair: SymmetricKeyModel) = symmetricKeyDao.delete(symmetricKeyPair)

}