package de.stubbe.jaem_client.model

import de.stubbe.jaem_client.database.entries.AsymmetricKeyPairModel
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.database.entries.SymmetricKeyModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.utils.toByteArray
import kotlinx.serialization.Serializable

@Serializable
data class ShareProfileModel(
    val uid: String,
    val name: String,
    val profilePicture: ByteArray?,
    val description: String,
    val asymmetricKeyPairs: List<AsymmetricKeyPairModel>,
    val symmetricKeys: List<SymmetricKeyModel>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShareProfileModel

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (profilePicture != null) {
            if (other.profilePicture == null) return false
            if (!profilePicture.contentEquals(other.profilePicture)) return false
        } else if (other.profilePicture != null) return false
        if (description != other.description) return false
        if (asymmetricKeyPairs != other.asymmetricKeyPairs) return false
        if (symmetricKeys != other.symmetricKeys) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        result = 31 * result + description.hashCode()
        result = 31 * result + asymmetricKeyPairs.hashCode()
        result = 31 * result + symmetricKeys.hashCode()
        return result
    }

    companion object {
        fun fromProfilePresentationModel(
            profile: ProfilePresentationModel,
            asymmetricKeyPairs: List<AsymmetricKeyPairModel>,
            symmetricKeys: List<SymmetricKeyModel>
        ): ShareProfileModel {
            return ShareProfileModel(
                uid = profile.profile.uid,
                name = profile.name,
                profilePicture = profile.profilePicture?.toByteArray(),
                description = profile.description,
                asymmetricKeyPairs = asymmetricKeyPairs,
                symmetricKeys = symmetricKeys
            )
        }

        fun fromProfileModel(
            profile: ProfileModel,
            asymmetricKeyPairs: List<AsymmetricKeyPairModel>,
            symmetricKeys: List<SymmetricKeyModel>
        ): ShareProfileModel {
            return ShareProfileModel(
                uid = profile.uid,
                name = profile.name,
                profilePicture = profile.profilePicture,
                description = profile.description,
                asymmetricKeyPairs = asymmetricKeyPairs,
                symmetricKeys = symmetricKeys
            )
        }
    }
}
