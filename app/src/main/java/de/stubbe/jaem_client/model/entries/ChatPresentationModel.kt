package de.stubbe.jaem_client.model.entries

import de.stubbe.jaem_client.database.entries.ChatEntity
import de.stubbe.jaem_client.database.entries.MessageEntity
import java.time.Instant
import java.time.ZoneId

/**
 * Model zur Darstellung von Chats
 *
 * @param profilePicture: Profilbild des Chats
 * @param name: Name des Chats
 * @param unreadMessages: Letzte Nachrichten im Chat
 * @param streak: Anzahl der aufeinanderfolgenden Nachrichten
 */
data class ChatPresentationModel(
    val profilePicture: ByteArray?,
    val name: String,
    val lastMessage: MessageEntity?,
    val unreadMessages: List<MessageEntity>,
    val streak: Int,
    val chat: ChatEntity
) {
    companion object {

        fun calculateStreak(chatMessages: List<MessageEntity>): Int {
            val uniqueDays = chatMessages
                .map { message -> Instant.ofEpochSecond(message.sendTime).atZone(ZoneId.systemDefault()).toLocalDate() }
                .toSortedSet()

            if (uniqueDays.isEmpty()) return 0

            var longestStreak = 1
            var currentStreak = 1
            val daysList = uniqueDays.toList()

            for (i in 1 until daysList.size) {
                if (daysList[i] == daysList[i - 1].plusDays(1)) {
                    currentStreak++
                } else {
                    longestStreak = maxOf(longestStreak, currentStreak)
                    currentStreak = 1
                }
            }

            return maxOf(longestStreak, currentStreak)
        }

    }
}