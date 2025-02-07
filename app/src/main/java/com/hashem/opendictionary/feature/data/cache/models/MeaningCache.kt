package com.hashem.opendictionary.feature.data.cache.models

import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeaningCache(
    val definitions: List<DefinitionCache>,
    val synonyms: Set<String>,
    val antonyms: Set<String>
) {
    fun toMeaning(): Meaning {
        return Meaning(
            definitions = definitions.map { it.toDefinition() },
            synonyms = synonyms,
            antonyms = antonyms
        )
    }
}
fun Meaning.toMeaningCache(): MeaningCache {
    return MeaningCache(
        definitions = definitions.map { it.toDefinitionCache() },
        synonyms = synonyms,
        antonyms = antonyms
    )
}

@Serializable
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
fun Definition.toDefinitionCache(): DefinitionCache {
    return DefinitionCache(
        definition = definition,
        example = example
    )
}