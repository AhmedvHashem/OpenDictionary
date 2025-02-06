package com.hashem.opendictionary.feature.data.cache.models

import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class MeaningCache(
    val partOfSpeech: String,
    val definitions: List<DefinitionCache>,
    val synonyms: List<String>,
    val antonyms: List<String>
) {
    fun toMeaning(): Meaning {
        return Meaning(
            partOfSpeech = partOfSpeech,
            definitions = definitions.map { it.toDefinition() },
            synonyms = synonyms,
            antonyms = antonyms
        )
    }
}

data class DefinitionCache(
    val definition: String,
    val example: String,
) {
    fun toDefinition(): Definition {
        return Definition(
            definition = definition,
            example = example
        )
    }
}