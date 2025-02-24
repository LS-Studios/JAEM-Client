package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "symmetric_keys")
data class SymmetricKeyModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val key: String,
    @ColumnInfo(name = "profile_id")
    val profileId: Int,
    val encryption: SymmetricEncryption,
)
