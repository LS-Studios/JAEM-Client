package de.stubbe.jaem_client.model

import de.stubbe.jaem_client.data.INT_BYTES
import de.stubbe.jaem_client.data.TIMESTAMP_LENGTH
import de.stubbe.jaem_client.data.UID_LENGTH
import de.stubbe.jaem_client.database.entries.ChatEntity
import de.stubbe.jaem_client.database.entries.EncryptionKeyEntity
import de.stubbe.jaem_client.database.entries.ProfileEntity
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.model.enums.KeyType
import de.stubbe.jaem_client.model.network.UDSUserDto
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.utils.base64StringToByteArray
import de.stubbe.jaem_client.utils.getUnixTime
import de.stubbe.jaem_client.utils.toByteArray
import de.stubbe.jaem_client.utils.toInt
import de.stubbe.jaem_client.utils.toLong

data class ShareProfileModel(
    val uid: String,
    val name: String,
    val profilePicture: ByteArray?,
    val description: String,
    val allowProfileSharing: Boolean = true,
    val keys: List<EncryptionKeyEntity>,
    val timestamp: Long
) {

    fun toByteArray(): ByteArray {
        val nameBytes = name.length.toByteArray() + name.toByteArray()
        val profilePictureBytes = profilePicture?.let { it.size.toByteArray() + profilePicture } ?: byteArrayOf(0)
        val descriptionBytes = description.length.toByteArray() + description.toByteArray()
        val allowProfileSharingBytes = byteArrayOf(if (allowProfileSharing) 1 else 0)
        val keyBytes = keys.fold(ByteArray(0)) { acc, key -> acc + key.key.size.toByteArray() + key.key }
        val timestamp = timestamp.toByteArray()
        return uid.toByteArray() + nameBytes + profilePictureBytes + descriptionBytes + allowProfileSharingBytes + keyBytes + timestamp
    }

    companion object {
        suspend fun addSharedProfileToDB(
            sharedProfile: ShareProfileModel,
            profileRepository: ProfileRepository,
            encryptionKeyRepository: EncryptionKeyRepository,
            chatRepository: ChatRepository,
            deviceClient: ED25519Client
        ): Long {
            val newProfile = ProfileEntity(
                id = 0,
                uid = sharedProfile.uid,
                profilePicture = sharedProfile.profilePicture,
                name = sharedProfile.name,
                description = sharedProfile.description,
                allowProfileSharing = sharedProfile.allowProfileSharing
            )

            profileRepository.insertProfile(newProfile)

            encryptionKeyRepository.insertAllEncryptionKeys(
                sharedProfile.keys
            )

            return chatRepository.insertChat(
                ChatEntity(
                    id = 0,
                    profileUid = deviceClient.profileUid!!,
                    chatPartnerUid = newProfile.uid,
                )
            )
        }

        fun fromProfileModel(profile: ProfileEntity, keys: List<EncryptionKeyEntity>): ShareProfileModel {
            return ShareProfileModel(
                uid = profile.uid,
                name = profile.name,
                profilePicture = profile.profilePicture,
                description = profile.description,
                keys = keys,
                timestamp = getUnixTime()
            )
        }

        fun fromProfilePresentationModel(
            profile: ProfilePresentationModel,
            keys: List<EncryptionKeyEntity>
        ): ShareProfileModel {
            return ShareProfileModel(
                uid = profile.profile.uid,
                name = profile.name,
                profilePicture = profile.profilePicture,
                description = profile.description,
                keys = keys,
                timestamp = getUnixTime()
            )
        }

        fun fromUDSUserDto(udsUserDto: UDSUserDto): ShareProfileModel {
            return ShareProfileModel(
                uid = udsUserDto.uid,
                name = udsUserDto.username,
                profilePicture = udsUserDto.profilePicture?.base64StringToByteArray(),
                description = udsUserDto.description ?: "",
                allowProfileSharing = true,
                keys = udsUserDto.publicKeys.map { publicKey ->
                    listOf(
                        EncryptionKeyEntity(
                            key = publicKey.signatureKey.base64StringToByteArray(),
                            type = KeyType.PUBLIC_ED25519,
                            profileUid = udsUserDto.uid
                        ),
                        EncryptionKeyEntity(
                            key = publicKey.exchangeKey.base64StringToByteArray(),
                            type = KeyType.PUBLIC_X25519,
                            profileUid = udsUserDto.uid
                        ),
                        EncryptionKeyEntity(
                            key = publicKey.rsaKey.base64StringToByteArray(),
                            type = KeyType.PUBLIC_RSA,
                            profileUid = udsUserDto.uid
                        )
                    )
                }.reduce({ acc, list -> acc + list }),
                timestamp = getUnixTime()
            )
        }

        fun fromByteArray(byteArray: ByteArray): ShareProfileModel {
            var offset = 0
            val uid = String(byteArray.copyOfRange(offset, UID_LENGTH))
            offset += UID_LENGTH
            val nameSize = byteArray.copyOfRange(offset, offset + INT_BYTES).toInt()
            offset += INT_BYTES
            val name = String(byteArray.copyOfRange(offset, offset + nameSize))
            offset += nameSize
            val profilePictureSize = byteArray.copyOfRange(offset, offset + INT_BYTES).toInt()
            offset += INT_BYTES
            val profilePicture = if (profilePictureSize == 0) null else byteArray.copyOfRange(offset, offset + profilePictureSize)
            offset += profilePictureSize
            val descriptionSize = byteArray.copyOfRange(offset, offset + INT_BYTES).toInt()
            offset += INT_BYTES
            val description = String(byteArray.copyOfRange(offset, offset + descriptionSize))
            offset += descriptionSize
            val allowProfileSharing = byteArray[offset] == 1.toByte()
            offset++
            val keys = mutableListOf<EncryptionKeyEntity>()
            while (keys.size < 3 && offset < byteArray.size) {
                val keySize = byteArray.copyOfRange(offset, offset + INT_BYTES).toInt()
                offset += INT_BYTES
                val key = byteArray.copyOfRange(offset, offset + keySize)
                offset += keySize
                val keyType = when (keys.size) {
                    0 -> KeyType.PUBLIC_ED25519
                    1 -> KeyType.PUBLIC_X25519
                    2 -> KeyType.PUBLIC_RSA
                    else -> throw Exception("Too many keys")
                }
                keys.add(EncryptionKeyEntity(
                    key = key,
                    type = keyType,
                    profileUid = uid
                ))
            }
            val timestamp = byteArray.copyOfRange(offset, offset + TIMESTAMP_LENGTH).toLong()
            return ShareProfileModel(uid, name, profilePicture, description, allowProfileSharing, keys, timestamp)
        }
    }

}