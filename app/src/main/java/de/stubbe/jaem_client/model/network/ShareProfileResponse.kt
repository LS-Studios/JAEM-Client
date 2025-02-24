package de.stubbe.jaem_client.model.network

import com.google.gson.annotations.SerializedName
import de.stubbe.jaem_client.model.ShareProfileModel

/**
 * Antwort auf das erstellen oder abrufen eines geteilten Profils
 *
 * @param profileUid Die uid des geteilten Profils
 * @param profile Das geteilte Profil (Ist null, wenn das Profil erstellt wurde)
 * @param createdAt Der Zeitpunkt der Erstellung
 */
data class ShareProfileResponse(
    @SerializedName("profile_uid")
    val profileUid: String,
    @SerializedName("profile")
    val profile: ShareProfileModel?,
    @SerializedName("created_at")
    val createdAt: Long,
)