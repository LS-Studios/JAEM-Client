package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.entries.ChatModel
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {

    suspend fun getChatById(id: Int) = chatDao.getChatById(id)

    fun getChatByIdWithChange(id: Int) = chatDao.getChatByIdWithChange(id)

    suspend fun getChatByProfileUid(profileUid: String) = chatDao.getChatByProfileUid(profileUid)

    suspend fun getChatByChatPartnerUid(chatPartnerUid: String) = chatDao.getChatByChatPartnerUid(chatPartnerUid)

    fun getAllChats() = chatDao.getAllChats()

    suspend fun insertChat(chat: ChatModel) = chatDao.insert(chat)

    suspend fun updateChat(chat: ChatModel) = chatDao.update(chat)

    suspend fun deleteChat(chat: ChatModel) = chatDao.delete(chat)

}