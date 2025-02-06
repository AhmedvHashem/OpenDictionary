package com.hashem.opendictionary.common.database

import androidx.room.TypeConverter
import com.hashem.opendictionary.feature.data.cache.models.MeaningCache
import com.hashem.opendictionary.feature.data.cache.models.PhoneticCache
import kotlinx.serialization.json.Json

class Converter {

    @TypeConverter
    fun fromPhoneticCacheToString(value: PhoneticCache): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToPhoneticCache(value: String): PhoneticCache {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromStringToMeaningCache(value: String): List<MeaningCache> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMeaningCacheToString(value: List<MeaningCache>): String {
        return Json.encodeToString(value)
    }
}