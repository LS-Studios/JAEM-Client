package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.ChatRequestModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die ChatRequest Datenbank.
 */
@Dao
abstract class ChatRequestDao: BaseDao<ChatRequestModel> {

    @Query("SELECT * FROM chat_requests WHERE id = :id")
    abstract suspend fun getChatRequestById(id: Int): ChatRequestModel

    @Query("SELECT * FROM chat_requests WHERE id = :id")
    abstract fun getChatRequestByIdWithChange(id: Int): Flow<ChatRequestModel>

    @Query("SELECT * FROM chat_requests")
    abstract fun getAllChatRequests(): Flow<List<ChatRequestModel>>

}