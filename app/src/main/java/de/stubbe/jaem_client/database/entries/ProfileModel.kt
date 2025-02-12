package de.stubbe.jaem_client.database.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  Model zur Speicherung von Profilinformationen
 *  @param id: Eindeutige ID des Profils
 *  @param name: Name des Benutzers
 *  @param image: URL oder Pfad zum Profilbild
 *  @param description: Beschreibung oder Status des Benutzers
 */
@Entity(tableName = "profiles")
data class ProfileModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val image: String,
    val description: String
)