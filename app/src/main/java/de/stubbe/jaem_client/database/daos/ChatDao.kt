package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.ChatModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Chat Datenbank.
 */
@Dao
abstract class ChatDao: BaseDao<ChatModel> {

    @Query("SELECT * FROM chats WHERE id = :id")
    abstract suspend fun getChatById(id: Int): ChatModel

    @Query("SELECT * FROM chats WHERE id = :id")
    abstract fun getChatByIdWithChange(id: Int): Flow<ChatModel>

    @Query("SELECT * FROM chats")
    abstract fun getAllChats(): Flow<List<ChatModel>>

}