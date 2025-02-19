package de.stubbe.jaem_client.repositories

import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.entries.ChatModel

/**
 * Repository für die Chat Datenbank.
 */
class ChatRepository(private val chatDao: ChatDao) {

    suspend fun getChatById(id: Int) = chatDao.getChatById(id)

    fun getChatByIdWithChange(id: Int) = chatDao.getChatByIdWithChange(id)

    fun getAllChats() = chatDao.getAllChats()

    suspend fun insertChat(chat: ChatModel) = chatDao.insert(chat)

    suspend fun updateChat(chat: ChatModel) = chatDao.update(chat)

    suspend fun deleteChat(chat: ChatModel) = chatDao.delete(chat)

}