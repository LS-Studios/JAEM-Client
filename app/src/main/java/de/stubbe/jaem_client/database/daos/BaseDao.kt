package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface BaseDao<T> {

    @Insert
    suspend fun insert(vararg item: T)

    @Update
    suspend fun update(vararg item: T)

    @Delete
    suspend fun delete(vararg item: T)

}