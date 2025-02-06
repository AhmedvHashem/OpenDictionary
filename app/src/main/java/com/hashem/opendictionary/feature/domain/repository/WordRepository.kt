package com.hashem.opendictionary.feature.domain.repository

import com.hashem.opendictionary.feature.domain.models.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {

    fun getWord(word: String): Flow<Result<Word>>

    fun getRecentSearchWords(): Flow<Result<List<Word>>>
}