package de.stubbe.jaem_client.database.entries

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.model.network.PublicKeyDto
import de.stubbe.jaem_client.model.network.UDSUserDto
import kotlinx.serialization.Serializable

@Entity(tableName = "uds_users")
data class UDSUserEntity(
    @PrimaryKey
    val id: Int,
    val uid : String,
    val username : String,
    val publicKeys : List<PublicKeyEntity>,
    val profilePicture: String?,
    val description: String
) {
    companion object {

        fun fromDto(dto: UDSUserDto): UDSUserEntity {
            return UDSUserEntity(
                dto.id,
                dto.uid,
                dto.username,
                dto.publicKeys.map { PublicKeyEntity.fromDto(it) },
                dto.profilePicture,
                dto.description ?: ""
            )
        }

    }
}

@Serializable
data class PublicKeyEntity(
    val algorithm: SymmetricEncryption,
    val signatureKey: String,
    val exchangeKey: String,
    val rsaKey: String
) {
    companion object {

        fun fromDto(dto: PublicKeyDto): PublicKeyEntity {
            return PublicKeyEntity(
                dto.algorithm,
                dto.signatureKey,
                dto.exchangeKey,
                dto.rsaKey
            )
        }

    }
}
