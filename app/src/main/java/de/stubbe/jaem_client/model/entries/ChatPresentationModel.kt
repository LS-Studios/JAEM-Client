package de.stubbe.jaem_client.model.entries

import android.graphics.Bitmap
import de.stubbe.jaem_client.database.entries.ChatEntity
import de.stubbe.jaem_client.database.entries.MessageEntity

/**
 * Model zur Darstellung von Chats
 *
 * @param profilePicture: Profilbild des Chats
 * @param name: Name des Chats
 * @param unreadMessages: Letzte Nachrichten im Chat
 * @param streak: Anzahl der aufeinanderfolgenden Nachrichten
 */
data class ChatPresentationModel(
    val profilePicture: Bitmap?,
    val name: String,
    val lastMessage: MessageEntity?,
    val unreadMessages: List<MessageEntity>,
    val streak: Int,
    val chat: ChatEntity
)