package com.hashem.opendictionary.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
import com.hashem.opendictionary.feature.data.cache.models.WordCache

@Database(entities = [WordCache::class], exportSchema = false, version = 1)
@TypeConverters(Converter::class)
abstract class OpenDictionaryDatabase : RoomDatabase() {
    abstract fun dataSource(): WordCacheDataSource

    companion object {
        private const val DATABASE_NAME = "open-dictionary-database"

        private var INSTANCE: OpenDictionaryDatabase? = null
        fun getInstance(context: Context): OpenDictionaryDatabase {
            return INSTANCE ?: provideDatabase(context).also { INSTANCE = it }
        }

        private fun provideDatabase(context: Context): OpenDictionaryDatabase {
            return Room.databaseBuilder(
                context,
                OpenDictionaryDatabase::class.java, DATABASE_NAME
            ).build()
        }
    }
}