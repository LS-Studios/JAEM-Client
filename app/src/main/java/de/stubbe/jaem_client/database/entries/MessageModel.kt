package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.stubbe.jaem_client.model.Attachments
import kotlinx.serialization.Serializable

/**
 * Model zur Speicherung von Nachrichteninformationen
 *
 * @param id: Eindeutige ID der Nachricht
 * @param senderId: ID des Absenders (Profile)
 * @param receiverId: ID des Empfängers (Profile)
 * @param chatId: Zugehörige Chat-ID
 * @param stringContent: Textinhalt der Nachricht
 * @param attachments: Anhänge der Nachricht
 * @param sendTime: Zeitstempel des Sendens
 * @param deliveryTime: Zeitstempel der Zustellung (nullable)
 */
@Serializable
@Entity(tableName = "messages")
data class MessageModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "uid")
    val uid: String,
    @ColumnInfo(name = "sender_uid")
    val senderUid: String,
    @ColumnInfo(name = "receiver_uid")
    val receiverUid: String,
    @ColumnInfo(name = "chat_id")
    val chatId: Int,
    @ColumnInfo(name = "string_content")
    val stringContent: String?,
    @ColumnInfo(name = "attachments")
    val attachments: Attachments?,
    @ColumnInfo(name = "send_time")
    val sendTime: Long,
    @ColumnInfo(name = "delivery_time")
    val deliveryTime: Long?
)