package com.hashem.opendictionary.feature.data

import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
import com.hashem.opendictionary.feature.data.cache.models.WordCache
import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
import com.hashem.opendictionary.feature.data.remote.models.WordRemote
import com.hashem.opendictionary.feature.domain.models.Word
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordRepository
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.feature.domain.repository.asWordResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class WordRepository(
    private val remote: WordRemoteDataSource,
    private val cache: WordCacheDataSource
) : WordRepository {

    override fun getWord(word: String): Flow<WordResult<Word>> = flow {
        val wordFromCache = getWordFromCache(word).toWord()
        emit(wordFromCache)

    }.asWordResultFlow()

    override fun getRecentSearchWords(): Flow<WordResult<List<Word>>> = flow {
        val recentSearchWords = cache.getWords().map { it.toWord() }
        emit(recentSearchWords)
    }.asWordResultFlow()

    private suspend fun getWordsFromRemote(word: String): List<WordRemote> {
        try {
            val response = remote.getWords(word)
            if (response.isSuccessful) {
                return response.body() ?: throw WordError.NotFoundError
            } else {
                throw WordError.ApiError
            }
        } catch (e: IOException) {
            throw WordError.NetworkError
        } catch (e: Exception) {
            throw WordError.UnknownError("Fail: ${e.message}")
        }
    }

    private suspend fun getWordFromCache(word: String): WordCache {
        return cache.getWord(word) ?: throw WordError.NotFoundError
    }
}