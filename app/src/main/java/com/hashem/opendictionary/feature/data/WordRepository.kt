package com.hashem.opendictionary.feature.data

import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
import com.hashem.opendictionary.feature.data.cache.models.toWordCache
import com.hashem.opendictionary.feature.data.remote.DefaultNetworkErrorHandler
import com.hashem.opendictionary.feature.data.remote.NetworkErrorHandler
import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
import com.hashem.opendictionary.feature.data.remote.models.flat
import com.hashem.opendictionary.feature.domain.models.Word
import com.hashem.opendictionary.feature.domain.repository.WordRepository
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.feature.domain.repository.asWordResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WordRepository(
    private val remote: WordRemoteDataSource,
    private val cache: WordCacheDataSource,
    private val networkErrorHandler: NetworkErrorHandler = DefaultNetworkErrorHandler()
) : WordRepository {

    // TODO: Should be changed to return single word instead of list
    override fun getWord(word: String): Flow<WordResult<List<Word>>> = flow {
        val wordFromCache = cache.getWords(word)
        if (wordFromCache.isNotEmpty()) {
            emit(wordFromCache.map { it.toWord() })
        }

        val wordFromRemote = networkErrorHandler.handle { remote.getWords(word) }.flat()
        cache.insertWord(wordFromRemote.toWordCache())
        emit(listOf(wordFromRemote))
    }.asWordResultFlow()

    override fun getRecentSearchWords(): Flow<WordResult<List<Word>>> = flow {
        val recentSearchWords = cache.getWords().reversed().map { it.toWord() }
        emit(recentSearchWords)
    }.asWordResultFlow()
}