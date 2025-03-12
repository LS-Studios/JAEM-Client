package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  Model zur Speicherung von Profilinformationen
 *  @param id: Eindeutige ID des Profils in der lokalen Datenbank
 *  @param uid: Eindeutige ID des Profils für alle Benutzer
 *  @param name: Name des Benutzers
 *  @param profilePicture: URL oder Pfad zum Profilbild
 *  @param description: Beschreibung oder Status des Benutzers
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uid: String,
    val name: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val profilePicture: ByteArray?,
    val description: String,
    val allowProfileSharing: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileEntity

        if (id != other.id) return false
        if (uid != other.uid) return false
        if (name != other.name) return false
        if (!profilePicture.contentEquals(other.profilePicture)) return false
        if (description != other.description) return false
        if (allowProfileSharing != other.allowProfileSharing) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + profilePicture.contentHashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + allowProfileSharing.hashCode()
        return result
    }
}