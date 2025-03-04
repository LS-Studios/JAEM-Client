package de.stubbe.jaem_client.model.network

import UserData
import com.google.gson.annotations.SerializedName

/**
 * Antwort auf das erstellen oder abrufen eines geteilten Profils
 *
 * @param profileUid Die uid des geteilten Profils
 * @param profile Das geteilte Profil (Ist null, wenn das Profil erstellt wurde)
 * @param createdAt Der Zeitpunkt der Erstellung
 */
data class AddProfileResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserData
)



