package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.ChatRequestDao
import de.stubbe.jaem_client.database.entries.ChatRequestModel
import javax.inject.Inject

class ChatRequestRepository @Inject constructor(
    private val chatDao: ChatRequestDao
) {

    fun getAllChatRequests() = chatDao.getAllChatRequests()

    suspend fun insertChatRequest(chat: ChatRequestModel) = chatDao.insert(chat)

    suspend fun updateChatRequest(chat: ChatRequestModel) = chatDao.update(chat)

    suspend fun deleteChatRequest(chat: ChatRequestModel) = chatDao.delete(chat)

}