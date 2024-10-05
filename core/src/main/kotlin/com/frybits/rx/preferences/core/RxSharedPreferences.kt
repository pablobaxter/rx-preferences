@file:Suppress("unused")

package com.frybits.rx.preferences.core

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.RestrictTo
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

/*
 *  Copyright 2014 Prateek Srivastava
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Created by Pablo Baxter (Github: pablobaxter)
 */

/**
 * Preference factory class to provide [Preference] objects used reactive frameworks
 *
 * @param sharedPreferences The preferences that back this factory
 */
class RxSharedPreferences private constructor(
    val sharedPreferences: SharedPreferences
) {

    companion object {

        /**
         * Creates a [RxSharedPreferences] backed by the [SharedPreferences] passed in
         *
         * @return A new instance of [RxSharedPreferences]
         */
        @JvmStatic
        @JvmName("create")
        fun SharedPreferences.asRxSharedPreferences(): RxSharedPreferences {
            return RxSharedPreferences(this)
        }
    }

    private val keyChangedStreams: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    @JvmSynthetic
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun <T : Any> getOrCreateKeyChangedStream(key: String, streamCreator: () -> T): T {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            keyChangedStreams.computeIfAbsent(key) { streamCreator() }
        } else {
            keyChangedStreams.getOrPut(key) {
                return@getOrPut streamCreator()
            }
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    /** Creates a [Boolean] preference for the [key] with a default of `false` */
    @CheckResult
    fun getBoolean(key: String?): Preference<Boolean> {
        return getBoolean(key, false)
    }

    /** Creates a [Boolean] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    fun getBoolean(key: String?, defaultValue: Boolean): Preference<Boolean> {
        return Preference(
            this,
            key,
            defaultValue,
            BooleanAdapter
        )
    }

    /** Creates a [T] preference for the [key] using the enum class type [T] with a default of [defaultValue] */
    @CheckResult
    inline fun <reified T : Enum<T>> getEnum(
        key: String?,
        defaultValue: T
    ): Preference<T> {
        return getEnum(key, defaultValue, T::class.java)
    }

    /** Creates a [T] preference for the [key] using the enum class [clazz] with a default of [defaultValue] */
    @CheckResult
    fun <T : Enum<T>> getEnum(key: String?, defaultValue: T, clazz: Class<T>): Preference<T> {
        return Preference(
            this,
            key,
            defaultValue,
            EnumAdapter(clazz)
        )
    }

    /** Creates a [Float] preference for the [key] with a default of `0F` */
    @CheckResult
    fun getFloat(key: String?): Preference<Float> {
        return getFloat(key, 0F)
    }

    /** Creates a [Float] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    fun getFloat(key: String?, defaultValue: Float): Preference<Float> {
        return Preference(this, key, defaultValue, FloatAdapter)
    }

    /** Creates a [Int] preference for the [key] with a default of `0` */
    @CheckResult
    fun getInteger(key: String?): Preference<Int> {
        return getInteger(key, 0)
    }

    /** Creates a [Int] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    fun getInteger(key: String?, defaultValue: Int): Preference<Int> {
        return Preference(this, key, defaultValue, IntegerAdapter)
    }

    /** Creates a [Long] preference for the [key] with a default of `0L` */
    @CheckResult
    fun getLong(key: String?): Preference<Long> {
        return getLong(key, 0L)
    }

    /** Creates a [Long] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    fun getLong(key: String?, defaultValue: Long): Preference<Long> {
        return Preference(this, key, defaultValue, LongAdapter)
    }

    @CheckResult
    @Deprecated(
        message = "Not used any longer. Use 'getObject()' with 'asOptional()' operator for handle nullable objects.",
        replaceWith = ReplaceWith(
            expression = "getObject(key, defaultValue, converter)"
        ),
        level = DeprecationLevel.WARNING
    )
    fun <T: Any> getObjectNonNull(
        key: String?,
        defaultValue: T,
        converter: Preference.Converter<T>
    ): Preference<T> {
        return getObject(key, defaultValue, converter)
    }

    /** Creates a [T] preference for the [key] using the [converter], and with a default of [defaultValue]. */
    @CheckResult
    fun <T> getObject(
        key: String?,
        defaultValue: T,
        converter: Preference.Converter<T>
    ): Preference<T> {
        return Preference(this, key, defaultValue, ConverterAdapter(converter))
    }

    /** Creates a [String] preference for the [key] with a default of an empty string */
    @CheckResult
    fun getString(key: String?): Preference<String?> {
        return getString(key, "") // Rx2 doesn't allow null values
    }

    /** Creates a [String] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    fun getString(key: String?, defaultValue: String?): Preference<String?> {
        return Preference(this, key, defaultValue, StringAdapter)
    }

    /** Creates a string [Set] preference for the [key] with a default of [emptySet] */
    @CheckResult
    fun getStringSet(key: String?): Preference<Set<String?>?> {
        return getStringSet(key, emptySet()) // Rx2 doesn't allow null values
    }

    /** Creates a string [Set] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    fun getStringSet(key: String?, defaultValue: Set<String?>?): Preference<Set<String?>?> {
        return Preference(this, key, defaultValue, StringSetAdapter)
    }

    /** Clears the underlying shared preferences */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    // Clear out any closeables created
    protected fun finalize() {
        keyChangedStreams.values.filterIsInstance<Closeable>().forEach { it.close() }
        keyChangedStreams.clear()
    }
}
