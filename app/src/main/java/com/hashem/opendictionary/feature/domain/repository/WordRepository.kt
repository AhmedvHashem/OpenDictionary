package com.hashem.opendictionary.feature.domain.repository

import com.hashem.opendictionary.feature.domain.models.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {

    fun getWord(word: String): Flow<WordResult<List<Word>>>

    fun getRecentSearchWords(): Flow<WordResult<List<Word>>>
}