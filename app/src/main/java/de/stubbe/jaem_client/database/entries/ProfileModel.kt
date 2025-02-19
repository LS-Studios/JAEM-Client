package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray?,
    val description: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileModel

        if (id != other.id) return false
        if (name != other.name) return false
        if (!image.contentEquals(other.image)) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}