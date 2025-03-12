package de.stubbe.jaem_client.model.entries

import de.stubbe.jaem_client.database.entries.ProfileEntity
import de.stubbe.jaem_client.model.ED25519Client

/**
 * Model zur Darstellung von Profilinformationen
 *
 * @param name: Name des Benutzers
 * @param profilePicture: Profilbild des Benutzers
 * @param description: Beschreibung oder Status des Benutzers
 * @param client: de.stubbe.jaem_client.model.ED25519Client zur Verschlüsselung
 * @param profile: Profilinformationen
 */
data class ProfilePresentationModel(
    val name: String,
    val profilePicture: ByteArray?,
    val description: String,
    val client: ED25519Client,
    val profile: ProfileEntity
)