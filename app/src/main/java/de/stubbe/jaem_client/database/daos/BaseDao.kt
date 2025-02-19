package de.stubbe.jaem_client.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

/**
 * Basisklasse für alle DAOs.
 * Sie enthält die grundlegenden Methoden zum Einfügen, Aktualisieren und Löschen von Einträgen.
 */
@Dao
interface BaseDao<T> {

    @Insert
    suspend fun insert(item: T): Long

    @Update
    suspend fun update(item: T)

    @Delete
    suspend fun delete(item: T)

}