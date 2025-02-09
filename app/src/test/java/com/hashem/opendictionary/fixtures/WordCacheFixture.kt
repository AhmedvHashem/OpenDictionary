package com.hashem.opendictionary.fixtures

import com.hashem.opendictionary.feature.data.cache.models.DefinitionCache
import com.hashem.opendictionary.feature.data.cache.models.MeaningCache
import com.hashem.opendictionary.feature.data.cache.models.PhoneticCache
import com.hashem.opendictionary.feature.data.cache.models.WordCache

object WordCacheFixture {

    fun createWordCache(
        word: String = "example",
        phonetic: PhoneticCache = createPhoneticCache(),
        meanings: Map<String, MeaningCache> = mapOf("noun" to createMeaningCache())
    ) = WordCache(word, phonetic, meanings)

    private fun createPhoneticCache(
        text: String = "phonetic",
        audio: String = "audio"
    ) = PhoneticCache(text, audio)

    private fun createMeaningCache(
        definitions: List<DefinitionCache> = listOf(createDefinitionCache()),
        synonyms: Set<String> = setOf("synonym"),
        antonyms: Set<String> = setOf("antonym")
    ) = MeaningCache(definitions, synonyms, antonyms)

    private fun createDefinitionCache(
        definition: String = "definition",
        example: String = "example"
    ) = DefinitionCache(definition, example)
}