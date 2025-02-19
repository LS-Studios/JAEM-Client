package de.stubbe.jaem_client.database.entries

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
    val senderId: Int,
    val receiverId: Int,
    val chatId: Int,
    val stringContent: String?,
    val filePath: String?,
    val sendTime: Long,
    val deliveryTime: Long?
)