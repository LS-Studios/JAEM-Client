package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.enums.KeyType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "symmetric_keys")
data class SymmetricKeyModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val key: ByteArray,
    @ColumnInfo(name = "device_id")
    val deviceId: Int,
    @ColumnInfo(name = "chat_partner_id")
    val chatPartnerId: Int,
    val type: KeyType,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SymmetricKeyModel

        if (id != other.id) return false
        if (!key.contentEquals(other.key)) return false
        if (deviceId != other.deviceId) return false
        if (chatPartnerId != other.chatPartnerId) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + key.contentHashCode()
        result = 31 * result + deviceId
        result = 31 * result + chatPartnerId
        result = 31 * result + type.hashCode()
        return result
    }
}
