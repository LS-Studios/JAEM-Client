package de.stubbe.jaem_client.database.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model zur Speicherung von Nachrichteninformationen
 *
 * @param id: Eindeutige ID der Nachricht
 * @param senderId: ID des Absenders (Profile)
 * @param receiverId: ID des Empfängers (Profile)
 * @param chatId: Zugehörige Chat-ID
 * @param stringContent: Textinhalt der Nachricht
 * @param filePath: Pfad zur Datei (nullable)
 * @param sendTime: Zeitstempel des Sendens
 * @param deliveryTime: Zeitstempel der Zustellung (nullable)
 */
@Entity(tableName = "messages")
data class MessageModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "sender_id")
    val senderId: Int,
    @ColumnInfo(name = "receiver_id")
    val receiverId: Int,
    @ColumnInfo(name = "chat_id")
    val chatId: Int,
    @ColumnInfo(name = "string_content")
    val stringContent: String?,
    @ColumnInfo(name = "file_path")
    val filePath: String?,
    @ColumnInfo(name = "send_time")
    val sendTime: Long,
    @ColumnInfo(name = "delivery_time")
    val deliveryTime: Long?
)