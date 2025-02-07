package com.hashem.opendictionary.feature.data.cache.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hashem.opendictionary.feature.domain.models.Word
import kotlinx.serialization.SerialName

@Entity
data class WordCache(
    @SerialName("word")
    @PrimaryKey
    val word: String,
    @SerialName("phonetic")
    val phonetic: PhoneticCache,
    @SerialName("meanings")
    val meanings: Map<String, MeaningCache>,
) {
    fun toWord(): Word {
        return Word(
            word = word,
            phonetic = phonetic.toPhonetic(),
            meanings = meanings.mapValues { it.value.toMeaning() }
        )
    }
}

fun Word.toWordCache(): WordCache {
    return WordCache(
        word = word,
        phonetic = phonetic.toPhoneticCache(),
        meanings = meanings.mapValues { it.value.toMeaningCache() }
    )
}
