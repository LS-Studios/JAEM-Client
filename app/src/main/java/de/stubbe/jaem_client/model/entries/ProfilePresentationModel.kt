package de.stubbe.jaem_client.model.entries

import android.graphics.Bitmap
import de.stubbe.jaem_client.database.entries.AsymmetricKeyPairModel
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.database.entries.SymmetricKeyModel

/**
 * Model zur Darstellung von Profilinformationen
 *
 * @param name: Name des Benutzers
 * @param profilePicture: Profilbild des Benutzers
 * @param description: Beschreibung oder Status des Benutzers
 * @param asymmetricKeyPairs: Liste der asymmetrischen Schlüsselpaare des Benutzers
 * @param symmetricKeys: Liste der symmetrischen Schlüssel des Benutzers
 */
data class ProfilePresentationModel(
    val name: String,
    val profilePicture: Bitmap?,
    val description: String,
    val asymmetricKeyPairs: List<AsymmetricKeyPairModel>,
    val symmetricKeys: List<SymmetricKeyModel>,
    val profile: ProfileModel
)
