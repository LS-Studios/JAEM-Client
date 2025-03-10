package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.stubbe.jaem_client.database.entries.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO für die Message Datenbank.
 */
@Dao
abstract class MessageDao: BaseDao<MessageEntity> {

    @Query("SELECT * FROM messages WHERE id = :id")
    abstract suspend fun getMessageById(id: Int): MessageEntity

    @Query("SELECT * FROM messages WHERE id = :id")
    abstract fun getMessageByIdWithChange(id: Int): Flow<MessageEntity>

    @Query("SELECT * FROM messages")
    abstract fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE uid = :uid")
    abstract fun deleteMessageByUid(uid: String)

}