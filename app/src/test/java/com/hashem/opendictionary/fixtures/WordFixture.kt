package com.hashem.opendictionary.fixtures

import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import com.hashem.opendictionary.feature.domain.models.Phonetic
import com.hashem.opendictionary.feature.domain.models.Word

object WordFixture {

    fun createWord(
        word: String = "example",
        phonetic: Phonetic = createPhonetic(),
        meanings: Map<String, Meaning> = mapOf("noun" to createMeaning())
    ) = Word(word, phonetic, meanings)

    private fun createPhonetic(
        text: String = "phonetic",
        audio: String = "audio"
    ) = Phonetic(text, audio)

    private fun createMeaning(
        definitions: List<Definition> = listOf(createDefinition()),
        synonyms: Set<String> = setOf("synonym"),
        antonyms: Set<String> = setOf("antonym")
    ) = Meaning(definitions, synonyms, antonyms)

    private fun createDefinition(
        definition: String = "definition",
        example: String = "example"
    ) = Definition(definition, example)
}