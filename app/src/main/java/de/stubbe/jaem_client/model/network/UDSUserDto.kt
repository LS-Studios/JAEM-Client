package de.stubbe.jaem_client.model.network

import com.google.gson.annotations.SerializedName
import de.stubbe.jaem_client.database.entries.ProfileEntity
import de.stubbe.jaem_client.database.entries.PublicKeyEntity
import de.stubbe.jaem_client.database.entries.UDSUserEntity
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.utils.toBase64String
import kotlinx.serialization.Serializable

@Serializable
data class UDSUserDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("uid")
    val uid : String,
    @SerializedName("username")
    val username : String,
    @SerializedName("public_keys")
    val publicKeys : List<PublicKeyDto>,
    @SerializedName("profile_picture")
    val profilePicture: String?,
    @SerializedName("description")
    val description: String?
) {
    companion object {

        fun fromProfile(profile: ProfileEntity, client: ED25519Client): UDSUserDto {
            return UDSUserDto(
                0,
                client.profileUid!!,
                profile.name,
                listOf(
                    PublicKeyDto(
                        client.encryption,
                        String(client.ed25519PublicKey!!.encoded),
                        String(client.x25519PublicKey!!.encoded),
                        String(client.rsaPublicKey!!.encoded)
                    )
                ),
                profile.profilePicture?.toBase64String(),
                profile.description
            )
        }

        fun fromEntity(dto: UDSUserEntity): UDSUserDto {
            return UDSUserDto(
                dto.id,
                dto.uid,
                dto.username,
                dto.publicKeys.map { PublicKeyDto.fromEntity(it) },
                dto.profilePicture,
                dto.description
            )
        }

    }
}

@Serializable
data class PublicKeyDto(
    @SerializedName("algorithm")
    val algorithm: SymmetricEncryption,
    @SerializedName("signature_key")
    val signatureKey: String,
    @SerializedName("exchange_key")
    val exchangeKey: String,
    @SerializedName("rsa_key")
    val rsaKey: String
) {
    companion object {

        fun fromEntity(dto: PublicKeyEntity): PublicKeyDto {
            return PublicKeyDto(
                dto.algorithm,
                dto.signatureKey,
                dto.exchangeKey,
                dto.rsaKey
            )
        }

    }
}