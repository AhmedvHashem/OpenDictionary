package com.hashem.opendictionary.feature.data.cache.models

import com.hashem.opendictionary.feature.domain.models.Phonetic
import kotlinx.serialization.Serializable

@Serializable
data class PhoneticCache(
    val text: String,
    val audio: String,
) {
    fun toPhonetic(): Phonetic {
        return Phonetic(
            text = text,
            audio = audio
        )
    }
}

fun Phonetic.toPhoneticCache(): PhoneticCache {
    return PhoneticCache(
        text = text,
        audio = audio
    )
}