package com.example.ardrawsketch.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(any: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreList(any: MutableList<T>): MutableList<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplaceList(any: List<T>): MutableList<Long>

    @Update
    suspend fun update(vararg any: T): Int

    @Transaction
    suspend fun insertOrUpdate(any: T): Int {
        return if (insertIgnore(any) == -1L) {
            update(any)
        } else return -1
    }

    @Transaction
    suspend fun insertOrUpdate(any: MutableList<T>) {
        val insertResult = insertIgnoreList(any)
        val updateList = mutableListOf<T>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(any[i])
            }
        }
        if (updateList.isNotEmpty()) {
            updateList.forEach {
                update(it)
            }
        }
    }

    @Delete
    suspend fun delete(vararg any: T)

    @Delete
    suspend fun delete(any: List<T>)
}