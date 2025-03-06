package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.enums.KeyType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "encryption_keys")
data class EncryptionKeyModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val key: ByteArray,
    @ColumnInfo(name = "key_type")
    val type: KeyType,
    @ColumnInfo(name = "profile_id")
    val profileId: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionKeyModel

        if (id != other.id) return false
        if (!key.contentEquals(other.key)) return false
        if (type != other.type) return false
        if (profileId != other.profileId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + key.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + profileId
        return result
    }
}