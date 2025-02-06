package com.hashem.opendictionary.feature.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hashem.opendictionary.feature.data.cache.models.WordCache

@Dao
interface WordCacheDataSource {

    @Query("SELECT * FROM WordCache")
    suspend fun getWords(): List<WordCache>

    @Query("SELECT * FROM WordCache WHERE word = :word")
    suspend fun getWord(word: String): WordCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: WordCache)
}