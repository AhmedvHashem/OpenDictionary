package com.hashem.opendictionary.fixtures

import com.hashem.opendictionary.feature.data.remote.models.DefinitionRemote
import com.hashem.opendictionary.feature.data.remote.models.MeaningRemote
import com.hashem.opendictionary.feature.data.remote.models.PhoneticRemote
import com.hashem.opendictionary.feature.data.remote.models.WordRemote

object WordRemoteFixture {

    fun createWordRemote(
        word: String = "example",
        phonetic: List<PhoneticRemote> = listOf(createPhoneticRemote()),
        meanings: List<MeaningRemote> = listOf(createMeaningRemote())
    ) = WordRemote(word, phonetic, meanings)

    private fun createPhoneticRemote(
        text: String = "phonetic", audio: String = "audio"
    ) = PhoneticRemote(text, audio)

    private fun createMeaningRemote(
        partOfSpeech: String = "noun",
        definitions: List<DefinitionRemote> = listOf(createDefinitionRemote()),
        synonyms: Set<String> = setOf("synonym"),
        antonyms: Set<String> = setOf("antonym")
    ) = MeaningRemote(partOfSpeech, definitions, synonyms, antonyms)

    private fun createDefinitionRemote(
        definition: String = "definition", example: String = "example"
    ) = DefinitionRemote(definition, example)
}