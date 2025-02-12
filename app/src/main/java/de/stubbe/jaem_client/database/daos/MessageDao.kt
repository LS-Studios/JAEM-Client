package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.MessageModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDao: BaseDao<MessageModel> {

    @Query("SELECT * FROM messages WHERE id = :id")
    abstract suspend fun getMessageById(id: Int): MessageModel

    @Query("SELECT * FROM messages")
    abstract fun getAllMessages(): Flow<List<MessageModel>>

}