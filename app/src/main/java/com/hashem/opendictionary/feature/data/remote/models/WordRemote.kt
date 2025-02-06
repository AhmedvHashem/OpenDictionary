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
    val phonetics: List<PhoneticRemote>?,
    @SerialName("meanings")
    val meanings: List<MeaningRemote>?,
) {
    fun toWord(): Word {
        return Word(
            word = word,
            phonetic = phonetics?.firstOrNull { it.text != null && it.audio != null }?.toPhonetic()
                ?: Phonetic("", ""),
            meanings = meanings?.map { it.toMeaning() } ?: emptyList()
        )
    }
}
