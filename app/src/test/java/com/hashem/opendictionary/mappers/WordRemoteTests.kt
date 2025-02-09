package com.hashem.opendictionary.mappers

import com.hashem.opendictionary.feature.data.remote.models.DefinitionRemote
import com.hashem.opendictionary.feature.data.remote.models.MeaningRemote
import com.hashem.opendictionary.feature.data.remote.models.PhoneticRemote
import com.hashem.opendictionary.feature.data.remote.models.WordRemote
import com.hashem.opendictionary.feature.data.remote.models.flat
import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import com.hashem.opendictionary.feature.domain.models.Phonetic
import com.hashem.opendictionary.feature.domain.models.Word
import kotlin.test.Test
import kotlin.test.assertEquals

class WordRemoteTests {
    @Test
    fun `toWord should map WordRemote to Word correctly`() {
        val wordRemote = WordRemote(
            word = "example",
            phonetics = listOf(PhoneticRemote("phonetic", "audio")),
            meanings = listOf(
                MeaningRemote(
                    "noun",
                    listOf(DefinitionRemote("definition", "example")),
                    setOf("synonym"),
                    setOf("antonym")
                )
            )
        )

        val expectedWord = Word(
            word = "example",
            phonetic = Phonetic("phonetic", "audio"),
            meanings = mapOf(
                "noun" to Meaning(
                    definitions = listOf(Definition("definition", "example")),
                    synonyms = setOf("synonym"),
                    antonyms = setOf("antonym")
                )
            )
        )

        val result = wordRemote.toWord()

        assertEquals(expectedWord, result)
    }

    @Test
    fun `toWord should handle null phonetics and meanings`() {
        val wordRemote = WordRemote(
            word = "example",
            phonetics = null,
            meanings = null
        )

        val expectedWord = Word(
            word = "example",
            phonetic = Phonetic("", ""),
            meanings = emptyMap()
        )

        val result = wordRemote.toWord()

        assertEquals(expectedWord, result)
    }

    @Test
    fun `flat should merge multiple WordRemote objects correctly`() {
        val wordRemotes = listOf(
            WordRemote(
                word = "example",
                phonetics = listOf(PhoneticRemote("phonetic1", "audio1")),
                meanings = listOf(
                    MeaningRemote(
                        "noun",
                        listOf(DefinitionRemote("definition1", "example1")),
                        setOf("synonym1"),
                        setOf("antonym1")
                    )
                )
            ),
            WordRemote(
                word = "example",
                phonetics = listOf(PhoneticRemote("phonetic2", "audio2")),
                meanings = listOf(
                    MeaningRemote(
                        "verb",
                        listOf(DefinitionRemote("definition2", "example2")),
                        setOf("synonym2"),
                        setOf("antonym2")
                    )
                )
            )
        )

        val expectedWord = Word(
            word = "example",
            phonetic = Phonetic("phonetic1", "audio1"),
            meanings = mapOf(
                "noun" to Meaning(
                    definitions = listOf(Definition("definition1", "example1")),
                    synonyms = setOf("synonym1"),
                    antonyms = setOf("antonym1")
                ),
                "verb" to Meaning(
                    definitions = listOf(Definition("definition2", "example2")),
                    synonyms = setOf("synonym2"),
                    antonyms = setOf("antonym2")
                )
            )
        )

        val result = wordRemotes.flat()

        assertEquals(expectedWord, result)
    }

    @Test
    fun `toPhonetic should map PhoneticRemote to Phonetic correctly`() {
        val phoneticRemote = PhoneticRemote(
            text = "testText",
            audio = "testAudio"
        )

        val expectedPhonetic = Phonetic(
            text = "testText",
            audio = "testAudio"
        )

        val result = phoneticRemote.toPhonetic()

        assertEquals(expectedPhonetic, result)
    }

    @Test
    fun `toPhonetic should handle null text and audio`() {
        val phoneticRemote = PhoneticRemote(
            text = null,
            audio = null
        )

        val expectedPhonetic = Phonetic(
            text = "",
            audio = ""
        )

        val result = phoneticRemote.toPhonetic()

        assertEquals(expectedPhonetic, result)
    }

    @Test
    fun `toMeaning should map MeaningRemote to Meaning correctly`() {
        val meaningRemote = MeaningRemote(
            partOfSpeech = "noun",
            definitions = listOf(DefinitionRemote("definition1", "example1")),
            synonyms = setOf("synonym1"),
            antonyms = setOf("antonym1")
        )

        val expectedMeaning = Meaning(
            definitions = listOf(Definition("definition1", "example1")),
            synonyms = setOf("synonym1"),
            antonyms = setOf("antonym1")
        )

        val result = meaningRemote.toMeaning()

        assertEquals(expectedMeaning, result)
    }

    @Test
    fun `toMeaning should handle null definitions, synonyms, and antonyms`() {
        val meaningRemote = MeaningRemote(
            partOfSpeech = "noun",
            definitions = null,
            synonyms = null,
            antonyms = null
        )

        val expectedMeaning = Meaning(
            definitions = emptyList(),
            synonyms = emptySet(),
            antonyms = emptySet()
        )

        val result = meaningRemote.toMeaning()

        assertEquals(expectedMeaning, result)
    }

    @Test
    fun `toDefinition should map DefinitionRemote to Definition correctly`() {
        val definitionRemote = DefinitionRemote(
            definition = "definition1",
            example = "example1"
        )

        val expectedDefinition = Definition(
            definition = "definition1",
            example = "example1"
        )

        val result = definitionRemote.toDefinition()

        assertEquals(expectedDefinition, result)
    }

    @Test
    fun `toDefinition should handle null example`() {
        val definitionRemote = DefinitionRemote(
            definition = "definition1",
            example = null
        )

        val expectedDefinition = Definition(
            definition = "definition1",
            example = ""
        )

        val result = definitionRemote.toDefinition()

        assertEquals(expectedDefinition, result)
    }
}