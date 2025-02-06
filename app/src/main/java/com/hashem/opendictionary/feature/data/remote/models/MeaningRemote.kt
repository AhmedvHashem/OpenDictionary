package com.hashem.opendictionary.feature.data.remote.models

import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeaningRemote(
    @SerialName("partOfSpeech")
    val partOfSpeech: String,
    @SerialName("definitions")
    val definitions: List<DefinitionRemote>?,
    @SerialName("synonyms")
    val synonyms: List<String>?,
    @SerialName("antonyms")
    val antonyms: List<String>?
) {
    fun toMeaning(): Meaning {
        return Meaning(
            partOfSpeech = partOfSpeech,
            definitions = definitions?.map { it.toDefinition() } ?: emptyList(),
            synonyms = synonyms ?: emptyList(),
            antonyms = antonyms ?: emptyList()
        )
    }
}

@Serializable
data class DefinitionRemote(
    @SerialName("definition")
    val definition: String,
    @SerialName("example")
    val example: String?,
) {
    fun toDefinition(): Definition {
        return Definition(
            definition = definition,
            example = example ?: ""
        )
    }
}