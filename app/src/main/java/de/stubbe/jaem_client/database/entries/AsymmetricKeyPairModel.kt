package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "asymmetric_key_pairs")
data class AsymmetricKeyPairModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name : String,
    @ColumnInfo(name = "public_key")
    val publicKey: String,
    @ColumnInfo(name = "private_key")
    val privateKey: String,
    @ColumnInfo(name = "profile_id")
    val profileId: Int,
    val encryption: AsymmetricEncryption,
)
