package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model zur Speicherung von Chatanfragen
 *
 * @param id: Eindeutige ID der Chatanfrage
 * @param profileId: ID des Profils, das die Anfrage gestellt hat
 * @param chatPartnerId: ID des Chatpartners
 */
@Entity(tableName = "chat_requests")
data class ChatRequestModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "public_key")
    val publicKey: String,
    @ColumnInfo(name = "profile_id")
    val profileId: Int,
    @ColumnInfo(name = "chat_partner_id")
    val chatPartnerId: Int
)