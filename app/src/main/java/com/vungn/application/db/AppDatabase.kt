package com.vungn.application.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ardrawsketch.util.converter.TheTypeConverters
import com.vungn.application.BuildConfig
import com.vungn.application.MyApplication
import com.vungn.application.db.dao.DemoDao
import com.vungn.application.model.data.entity.EntityDemo

@TypeConverters(TheTypeConverters::class)
@Database(
    entities = [EntityDemo::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun demoDao(): DemoDao

    companion object {
        private const val DATABASE_NAME = BuildConfig.DATABASE_NAME
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                instance = Room.databaseBuilder(
                    MyApplication.applicationContext(), AppDatabase::class.java, DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                return instance as AppDatabase
            }
        }
    }
}