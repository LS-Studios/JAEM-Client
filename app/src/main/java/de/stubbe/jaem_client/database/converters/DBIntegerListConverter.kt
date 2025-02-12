package de.stubbe.jaem_client.database.converters

import androidx.room.TypeConverter

class DBIntegerListConverter {

    @TypeConverter
    fun fromList(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<Int> {
        return data.split(",").map { it.toInt() }
    }

}