package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.database.daos.EncryptionKeyDao
import de.stubbe.jaem_client.database.entries.EncryptionKeyEntity
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.utils.toEd25519PrivateKey
import de.stubbe.jaem_client.utils.toEd25519PublicKey
import de.stubbe.jaem_client.utils.toRSAPrivateKey
import de.stubbe.jaem_client.utils.toRSAPublicKey
import de.stubbe.jaem_client.utils.toX25519PrivateKey
import de.stubbe.jaem_client.utils.toX25519PublicKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EncryptionKeyRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val encryptionKeyDao: EncryptionKeyDao
) {

    /**
     * Erstellung des Flows für den Client
     *
     * @param profileId Profil id, wenn Null wird die des DeviceClients verwendet
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getClientFlow(profileUid: String? = null, canCreateUserClient: Boolean = false): Flow<ED25519Client?> {
        return userPreferencesRepository.userPreferencesFlow.flatMapLatest { userPreferences ->
            val actualProfileUid = profileUid ?: userPreferences.userProfileUid

            encryptionKeyDao.getKeysFromProfileFlow(actualProfileUid).map { encryptionKeys ->
                val keysMap = encryptionKeys.associateBy { it.type }

                val ed25519PublicKey = keysMap[KeyType.PUBLIC_ED25519]?.key?.toEd25519PublicKey()
                val ed25519PrivateKey = keysMap[KeyType.PRIVATE_ED25519]?.key?.toEd25519PrivateKey()
                val x25519PublicKey = keysMap[KeyType.PUBLIC_X25519]?.key?.toX25519PublicKey()
                val x25519PrivateKey = keysMap[KeyType.PRIVATE_X25519]?.key?.toX25519PrivateKey()
                val rsaPublicKey = keysMap[KeyType.PUBLIC_RSA]?.key?.toRSAPublicKey()
                val rsaPrivateKey = keysMap[KeyType.PRIVATE_RSA]?.key?.toRSAPrivateKey()

                if (profileUid == null) {
                    if (listOf(ed25519PublicKey, ed25519PrivateKey, x25519PublicKey, x25519PrivateKey, rsaPublicKey, rsaPrivateKey).any { it != null }) {
                        ED25519Client(
                            actualProfileUid,
                            ed25519PublicKey!!, ed25519PrivateKey!!,
                            x25519PublicKey!!, x25519PrivateKey!!,
                            rsaPublicKey!!, rsaPrivateKey!!
                        )
                    } else if (canCreateUserClient) {
                        val client = ED25519Client(actualProfileUid)
                        insertNewClient(client, actualProfileUid)
                        client
                    } else {
                        null
                    }
                } else {
                    if (listOf(ed25519PublicKey, x25519PublicKey, rsaPublicKey).all { it != null }) {
                        ED25519Client(
                            profileUid = actualProfileUid,
                            ed25519PublicKey = ed25519PublicKey!!,
                            x25519PublicKey = x25519PublicKey!!,
                            rsaPublicKey = rsaPublicKey!!
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    suspend fun insertNewClient(newClient: ED25519Client, profileUid: String) {
        val keyPairs = listOfNotNull(
            newClient.ed25519PublicKey?.let { KeyType.PUBLIC_ED25519 to it.encoded },
            newClient.ed25519PrivateKey?.let { KeyType.PRIVATE_ED25519 to it.encoded },
            newClient.x25519PublicKey?.let { KeyType.PUBLIC_X25519 to it.encoded },
            newClient.x25519PrivateKey?.let { KeyType.PRIVATE_X25519 to it.encoded },
            newClient.rsaPublicKey?.let { KeyType.PUBLIC_RSA to it.encoded },
            newClient.rsaPrivateKey?.let { KeyType.PRIVATE_RSA to it.encoded }
        ).map { (type, keyBytes) ->
            EncryptionKeyEntity(
                key = keyBytes,
                type = type,
                profileUid = profileUid
            )
        }

        encryptionKeyDao.insertAll(keyPairs)
    }

    suspend fun insertEncryptionKey(encryptionKey: EncryptionKeyEntity) = encryptionKeyDao.insert(encryptionKey)

    suspend fun insertAllEncryptionKeys(encryptionKeys: List<EncryptionKeyEntity>) = encryptionKeyDao.insertAll(encryptionKeys)

    suspend fun updateEncryptionKey(encryptionKey: EncryptionKeyEntity) = encryptionKeyDao.update(encryptionKey)

    suspend fun deleteEncryptionKey(encryptionKey: EncryptionKeyEntity) = encryptionKeyDao.delete(encryptionKey)

}