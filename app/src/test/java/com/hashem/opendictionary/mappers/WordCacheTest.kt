package com.hashem.opendictionary.mappers

import com.hashem.opendictionary.feature.data.cache.models.DefinitionCache
import com.hashem.opendictionary.feature.data.cache.models.MeaningCache
import com.hashem.opendictionary.feature.data.cache.models.PhoneticCache
import com.hashem.opendictionary.feature.data.cache.models.WordCache
import com.hashem.opendictionary.feature.data.cache.models.toDefinitionCache
import com.hashem.opendictionary.feature.data.cache.models.toMeaningCache
import com.hashem.opendictionary.feature.data.cache.models.toPhoneticCache
import com.hashem.opendictionary.feature.data.cache.models.toWordCache
import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import com.hashem.opendictionary.feature.domain.models.Phonetic
import com.hashem.opendictionary.feature.domain.models.Word
import org.junit.Test
import kotlin.test.assertEquals

class WordCacheTest {

    @Test
    fun `toWord should map WordCache to Word correctly`() {
        val wordCache = WordCache(
            word = "testWord",
            phonetic = PhoneticCache("testPhonetic", "testAudio"),
            meanings = mapOf(
                "noun" to MeaningCache(
                    definitions = listOf(DefinitionCache("definition1", "example1")),
                    synonyms = setOf("synonym1"),
                    antonyms = setOf("antonym1")
                )
            )
        )

        val expectedWord = Word(
            word = "testWord",
            phonetic = Phonetic("testPhonetic", "testAudio"),
            meanings = mapOf(
                "noun" to Meaning(
                    definitions = listOf(Definition("definition1", "example1")),
                    synonyms = setOf("synonym1"),
                    antonyms = setOf("antonym1")
                )
            )
        )

        val result = wordCache.toWord()

        assertEquals(expectedWord, result)
    }

    @Test
    fun `toWordCache should map Word to WordCache correctly`() {
        val word = Word(
            word = "testWord",
            phonetic = Phonetic("testPhonetic", "testAudio"),
            meanings = mapOf(
                "noun" to Meaning(
                    definitions = listOf(Definition("definition1", "example1")),
                    synonyms = setOf("synonym1"),
                    antonyms = setOf("antonym1")
                )
            )
        )

        val expectedWordCache = WordCache(
            word = "testWord",
            phonetic = PhoneticCache("testPhonetic", "testAudio"),
            meanings = mapOf(
                "noun" to MeaningCache(
                    definitions = listOf(DefinitionCache("definition1", "example1")),
                    synonyms = setOf("synonym1"),
                    antonyms = setOf("antonym1")
                )
            )
        )

        val result = word.toWordCache()

        assertEquals(expectedWordCache, result)
    }

    @Test
    fun `toPhonetic should map PhoneticCache to Phonetic correctly`() {
        val phoneticCache = PhoneticCache(
            text = "testText",
            audio = "testAudio"
        )

        val expectedPhonetic = Phonetic(
            text = "testText",
            audio = "testAudio"
        )

        val result = phoneticCache.toPhonetic()

        assertEquals(expectedPhonetic, result)
    }

    @Test
    fun `toPhoneticCache should map Phonetic to PhoneticCache correctly`() {
        val phonetic = Phonetic(
            text = "testText",
            audio = "testAudio"
        )

        val expectedPhoneticCache = PhoneticCache(
            text = "testText",
            audio = "testAudio"
        )

        val result = phonetic.toPhoneticCache()

        assertEquals(expectedPhoneticCache, result)
    }

    @Test
    fun `toMeaning should map MeaningCache to Meaning correctly`() {
        val meaningCache = MeaningCache(
            definitions = listOf(DefinitionCache("definition1", "example1")),
            synonyms = setOf("synonym1"),
            antonyms = setOf("antonym1")
        )

        val expectedMeaning = Meaning(
            definitions = listOf(Definition("definition1", "example1")),
            synonyms = setOf("synonym1"),
            antonyms = setOf("antonym1")
        )

        val result = meaningCache.toMeaning()

        assertEquals(expectedMeaning, result)
    }

    @Test
    fun `toMeaningCache should map Meaning to MeaningCache correctly`() {
        val meaning = Meaning(
            definitions = listOf(Definition("definition1", "example1")),
            synonyms = setOf("synonym1"),
            antonyms = setOf("antonym1")
        )

        val expectedMeaningCache = MeaningCache(
            definitions = listOf(DefinitionCache("definition1", "example1")),
            synonyms = setOf("synonym1"),
            antonyms = setOf("antonym1")
        )

        val result = meaning.toMeaningCache()

        assertEquals(expectedMeaningCache, result)
    }

    @Test
    fun `toDefinition should map DefinitionCache to Definition correctly`() {
        val definitionCache = DefinitionCache(
            definition = "definition1",
            example = "example1"
        )

        val expectedDefinition = Definition(
            definition = "definition1",
            example = "example1"
        )

        val result = definitionCache.toDefinition()

        assertEquals(expectedDefinition, result)
    }

    @Test
    fun `toDefinitionCache should map Definition to DefinitionCache correctly`() {
        val definition = Definition(
            definition = "definition1",
            example = "example1"
        )

        val expectedDefinitionCache = DefinitionCache(
            definition = "definition1",
            example = "example1"
        )

        val result = definition.toDefinitionCache()

        assertEquals(expectedDefinitionCache, result)
    }
}