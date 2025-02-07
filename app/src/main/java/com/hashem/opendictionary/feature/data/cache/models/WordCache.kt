package com.hashem.opendictionary.feature.data.cache.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hashem.opendictionary.feature.domain.models.Word

@Entity
data class WordCache(
    @PrimaryKey
    val word: String,
    val phonetic: PhoneticCache,
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
