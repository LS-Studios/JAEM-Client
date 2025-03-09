package de.stubbe.jaem_client.model.entries

import android.graphics.Bitmap
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.MessageModel

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
    val lastMessage: MessageModel?,
    val unreadMessages: List<MessageModel>,
    val streak: Int,
    val chat: ChatModel
)