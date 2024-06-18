package com.example.ardrawsketch.util.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TheTypeConverters {
    @TypeConverter
    fun fromListLongToString(intList: List<Long>): String = intList.toString()

    @TypeConverter
    fun toListLongFromString(stringList: String): List<Long> {
        val result = ArrayList<Long>()
        val split = stringList.replace("[", "").replace("]", "").replace(" ", "").split(",")
        for (n in split) {
            try {
                result.add(n.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    @TypeConverter
    fun fromMapStringToString(map: Map<String, String>): String = Gson().toJson(map)

    @TypeConverter
    fun toMapStringFromString(stringMap: String): Map<String, String> {
        val typeToken = object : TypeToken<Map<String, String>>() {}
        val result = Gson().fromJson(stringMap, typeToken.type) as Map<String, String>
        return result
    }
}