package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model zur Speicherung von Chatinformationen
 *
 * @param id: Eindeutige ID des Chats
 * @param userIds: IDs der am Chat teilnehmenden Benutzer
 */
@Entity(tableName = "chats")
data class ChatModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "profile_uid")
    val profileUid: String,
    @ColumnInfo(name = "chat_partner_uid")
    val chatPartnerUid: String
)