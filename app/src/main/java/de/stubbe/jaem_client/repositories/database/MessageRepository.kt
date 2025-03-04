package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.entries.MessageModel
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {

    suspend fun getMessageById(id: Int) = messageDao.getMessageById(id)

    fun getMessageByIdWithChange(id: Int) = messageDao.getMessageByIdWithChange(id)

    fun getAllMessages() = messageDao.getAllMessages()

    suspend fun insertMessage(message: MessageModel) = messageDao.insert(message)

    suspend fun updateMessage(message: MessageModel) = messageDao.update(message)

    suspend fun deleteMessage(message: MessageModel) = messageDao.delete(message)

}