package com.example.tasky.feature_agenda.data.util

import com.example.tasky.feature_agenda.domain.util.JsonSerializer
import com.squareup.moshi.Moshi
import com.vanpra.composematerialdialogs.datetime.BuildConfig
import java.io.IOException

class MoshiSerializer(
    private val moshi: Moshi
): JsonSerializer {
    override fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return try {
            moshi
                .adapter(clazz)
                .fromJson(json)
        } catch(e: IOException) {
            if(BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            null
        }
    }

    override fun <T> toJson(obj: T, clazz: Class<T>): String {
        return moshi
            .adapter(clazz)
            .toJson(obj)
    }
}