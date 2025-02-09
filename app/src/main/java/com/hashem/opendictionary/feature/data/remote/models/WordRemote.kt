package com.hashem.opendictionary.feature.data.remote.models

import com.hashem.opendictionary.feature.domain.models.Phonetic
import com.hashem.opendictionary.feature.domain.models.Word
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WordRemote(
    @SerialName("word")
    val word: String,
    @SerialName("phonetics")
    var phonetics: List<PhoneticRemote>? = null,
    @SerialName("meanings")
    var meanings: List<MeaningRemote>? = null,
) {
    fun toWord(): Word {
        return Word(
            word = word,
            phonetic = phonetics?.firstOrNull { !it.text.isNullOrEmpty() }?.apply {
                audio = phonetics?.firstOrNull { !it.audio.isNullOrEmpty() }?.audio ?: ""
            }?.toPhonetic() ?: Phonetic("", ""),
            meanings = meanings?.associateBy({ it.partOfSpeech }, { it.toMeaning() }) ?: emptyMap()
        )
    }
}


/**
 * Flattens a list of WordRemote objects into a single Word object by combining their properties.
 *
 * The flattening process:
 * 1. Converts each WordRemote to Word
 * 2. Combines all Words by:
 *    - Using the first non-empty phonetic value encountered
 *    - Merging meanings by part of speech, where for each part of speech:
 *      - If it's new, adds it directly
 *      - If it exists, combines the definitions, synonyms, and antonyms
 *
 * @return A single Word object containing the combined information from all WordRemote objects
 */
fun List<WordRemote>.flat(): Word {
    return map { it.toWord() }.reduce { accWord, newWord ->
        accWord.copy(
            phonetic = accWord.phonetic.takeUnless { it.isEmpty() } ?: newWord.phonetic,
            meanings = accWord.meanings.toMutableMap().apply {
                newWord.meanings.forEach { (partOfSpeech, meaning) ->
                    val existingMeaning = this[partOfSpeech]
                    if (existingMeaning == null) {
                        this[partOfSpeech] = meaning
                    } else {
                        this[partOfSpeech] = existingMeaning.copy(
                            definitions = existingMeaning.definitions + meaning.definitions,
                            synonyms = (existingMeaning.synonyms + meaning.synonyms),
                            antonyms = (existingMeaning.antonyms + meaning.antonyms)
                        )
                    }
                }
            }
        )
    }
}