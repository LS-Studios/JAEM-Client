package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.entries.MessageEntity
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {

    suspend fun getMessageById(id: Int) = messageDao.getMessageById(id)

    fun getMessageByIdWithChange(id: Int) = messageDao.getMessageByIdWithChange(id)

    fun getAllMessages() = messageDao.getAllMessages()

    suspend fun insertMessage(message: MessageEntity) = messageDao.insert(message)

    suspend fun insertMessages(messages: List<MessageEntity>) = messageDao.insertAll(messages)

    suspend fun updateMessage(message: MessageEntity) = messageDao.update(message)

    suspend fun deleteMessage(message: MessageEntity) = messageDao.delete(message)

    suspend fun deleteMessageByUid(uid: String) = messageDao.deleteMessageByUid(uid)

}