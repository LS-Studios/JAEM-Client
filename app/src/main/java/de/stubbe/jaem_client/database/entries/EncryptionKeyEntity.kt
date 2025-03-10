package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.enums.KeyType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "encryption_keys")
data class EncryptionKeyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val key: ByteArray,
    @ColumnInfo(name = "key_type")
    val type: KeyType,
    @ColumnInfo(name = "profile_uid")
    val profileUid: String = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionKeyEntity

        if (id != other.id) return false
        if (!key.contentEquals(other.key)) return false
        if (type != other.type) return false
        if (profileUid != other.profileUid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + key.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + profileUid.hashCode()
        return result
    }
}