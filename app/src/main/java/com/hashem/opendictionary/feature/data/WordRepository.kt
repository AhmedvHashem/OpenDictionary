package com.hashem.opendictionary.feature.data

import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
import com.hashem.opendictionary.feature.data.cache.models.toWordCache
import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
import com.hashem.opendictionary.feature.data.remote.models.flat
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
        val wordFromCache = cache.getWord(word)
        if (wordFromCache != null) {
            emit(wordFromCache.toWord())
        }

        val wordFromRemote = getWordFromRemote(word)
        cache.insertWord(wordFromRemote.toWordCache())
        emit(wordFromRemote)
    }.asWordResultFlow()

    override fun getRecentSearchWords(): Flow<WordResult<List<Word>>> = flow {
        val recentSearchWords = cache.getWords().map { it.toWord() }
        emit(recentSearchWords)
    }.asWordResultFlow()

    private suspend fun getWordFromRemote(word: String): Word {
        try {
            val response = remote.getWords(word)
            if (response.isSuccessful) {
                return response.body()?.flat() ?: throw WordError.NotFoundError
            } else {
                throw WordError.ApiError
            }
        } catch (e: IOException) {
            throw WordError.NetworkError
        } catch (e: Exception) {
            throw WordError.UnknownError("Fail: ${e.message}")
        }
    }
}