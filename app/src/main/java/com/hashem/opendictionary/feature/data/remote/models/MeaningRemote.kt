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
    var definitions: List<DefinitionRemote>? = null,
    @SerialName("synonyms")
    var synonyms: Set<String>? = null,
    @SerialName("antonyms")
    var antonyms: Set<String>? = null
) {
    fun toMeaning(): Meaning {
        return Meaning(
            definitions = definitions?.map { it.toDefinition() } ?: emptyList(),
            synonyms = synonyms ?: emptySet(),
            antonyms = antonyms ?: emptySet(),
        )
    }
}

@Serializable
data class DefinitionRemote(
    @SerialName("definition")
    val definition: String,
    @SerialName("example")
    var example: String? = null,
) {
    fun toDefinition(): Definition {
        return Definition(
            definition = definition,
            example = example ?: ""
        )
    }
}