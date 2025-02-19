package de.stubbe.jaem_client.repositories

import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.entries.MessageModel

/**
 * Repository für die Message Datenbank.
 */
class MessageRepository(private val messageDao: MessageDao) {

    suspend fun getMessageById(id: Int) = messageDao.getMessageById(id)

    fun getMessageByIdWithChange(id: Int) = messageDao.getMessageByIdWithChange(id)

    fun getAllMessages() = messageDao.getAllMessages()

    suspend fun insertMessage(message: MessageModel) = messageDao.insert(message)

    suspend fun updateMessage(message: MessageModel) = messageDao.update(message)

    suspend fun deleteMessage(message: MessageModel) = messageDao.delete(message)

}