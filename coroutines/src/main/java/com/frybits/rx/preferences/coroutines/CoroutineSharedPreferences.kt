package com.frybits.rx.preferences.coroutines

import android.content.SharedPreferences
import androidx.annotation.CheckResult
import androidx.annotation.VisibleForTesting
import com.frybits.rx.preferences.core.BooleanAdapter
import com.frybits.rx.preferences.core.ConverterAdapter
import com.frybits.rx.preferences.core.EnumAdapter
import com.frybits.rx.preferences.core.FloatAdapter
import com.frybits.rx.preferences.core.BaseRxSharedPreferences
import com.frybits.rx.preferences.core.IntegerAdapter
import com.frybits.rx.preferences.core.LongAdapter
import com.frybits.rx.preferences.core.Preference
import com.frybits.rx.preferences.core.StringAdapter
import com.frybits.rx.preferences.core.StringSetAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

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
 * Preference factory class to provide [CoroutinePreference] objects
 */
class CoroutineSharedPreferences @VisibleForTesting internal constructor(
    sharedPreferences: SharedPreferences,
    overrideKeyChanges: Flow<String?>?,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
): BaseRxSharedPreferences(sharedPreferences) {

    companion object {

        /**
         * Creates a [CoroutineSharedPreferences] backed by the [SharedPreferences] passed in
         *
         * @param sharedPreferences The preferences to back this factory
         * @param keyChanges Optional [Flow] that this factory will use to listen for key changes
         *
         * @return A new instance of [CoroutineSharedPreferences]
         */
        @JvmStatic
        @JvmOverloads
        fun create(sharedPreferences: SharedPreferences, keyChanges: Flow<String?>? = null): CoroutineSharedPreferences {
            return CoroutineSharedPreferences(sharedPreferences, keyChanges)
        }
    }

    // Either use the key changes flow passed in, or the default flow
    private val keyChanges: Flow<String?> = overrideKeyChanges ?: callbackFlow<String?> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            check(prefs === sharedPreferences) { "CoroutinePreferences not listening to the right SharedPreferences" }
            trySendBlocking(key)
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.shareIn(scope, SharingStarted.WhileSubscribed())

    /** Creates a [Boolean] preference for the [key] with a default of `false` */
    @CheckResult
    fun getBoolean(key: String?): CoroutinePreference<Boolean> {
        return getBoolean(key, false)
    }

    /** Creates a [Boolean] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getBoolean(key: String?, defaultValue: Boolean): CoroutinePreference<Boolean> {
        return Preference(sharedPreferences, key, defaultValue, BooleanAdapter).asCoroutinePreference(keyChanges)
    }

    /** Creates a [T] preference for the [key] using the enum class type [T] with a default of [defaultValue] */
    @CheckResult
    inline fun <reified T: Enum<T>> getEnum(key: String?, defaultValue: T): CoroutinePreference<T> {
        return getEnum(key, defaultValue, T::class.java)
    }

    /** Creates a [T] preference for the [key] using the enum class [clazz] with a default of [defaultValue] */
    @CheckResult
    public override fun <T : Enum<T>> getEnum(key: String?, defaultValue: T, clazz: Class<T>): CoroutinePreference<T> {
        return Preference(sharedPreferences, key, defaultValue, EnumAdapter(clazz)).asCoroutinePreference(keyChanges)
    }

    /** Creates a [Float] preference for the [key] with a default of `0F` */
    @CheckResult
    fun getFloat(key: String?): CoroutinePreference<Float> {
        return getFloat(key, 0F)
    }

    /** Creates a [Float] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getFloat(key: String?, defaultValue: Float): CoroutinePreference<Float> {
        return Preference(sharedPreferences, key, defaultValue, FloatAdapter).asCoroutinePreference(keyChanges)
    }

    /** Creates a [Int] preference for the [key] with a default of `0` */
    @CheckResult
    fun getInteger(key: String?): CoroutinePreference<Int> {
        return getInteger(key, 0)
    }

    /** Creates a [Int] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getInteger(key: String?, defaultValue: Int): CoroutinePreference<Int> {
        return Preference(sharedPreferences, key, defaultValue, IntegerAdapter).asCoroutinePreference(keyChanges)
    }

    /** Creates a [Long] preference for the [key] with a default of `0L` */
    @CheckResult
    fun getLong(key: String?): CoroutinePreference<Long> {
        return getLong(key, 0L)
    }

    /** Creates a [Long] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getLong(key: String?, defaultValue: Long): CoroutinePreference<Long> {
        return Preference(sharedPreferences, key, defaultValue, LongAdapter).asCoroutinePreference(keyChanges)
    }

    /** Creates a [T] preference for the [key] using the [converter], and with a default of [defaultValue] */
    @CheckResult
    public override fun <T> getObject(key: String?, defaultValue: T, converter: Preference.Converter<T>): CoroutinePreference<T> {
        return Preference(sharedPreferences, key, defaultValue, ConverterAdapter(converter)).asCoroutinePreference(keyChanges)
    }

    /** Creates a [String] preference for the [key] with a default of `null` */
    @CheckResult
    fun getString(key: String?): CoroutinePreference<String?> {
        return getString(key, null)
    }

    /** Creates a [String] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getString(key: String?, defaultValue: String?): CoroutinePreference<String?> {
        return Preference(sharedPreferences, key, defaultValue, StringAdapter).asCoroutinePreference(keyChanges)
    }

    /** Creates a string [Set] preference for the [key] with a default of `null` */
    @CheckResult
    fun getStringSet(key: String?): CoroutinePreference<Set<String?>?> {
        return getStringSet(key, null)
    }

    /** Creates a string [Set] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getStringSet(key: String?, defaultValue: Set<String?>?): CoroutinePreference<Set<String?>?> {
        return Preference(sharedPreferences, key, defaultValue, StringSetAdapter).asCoroutinePreference(keyChanges)
    }

    /** Clears the underlying shared preferences with [SharedPreferences.Editor.commit] in [Dispatchers.IO] */
    @JvmSynthetic
    suspend fun clearSync(): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext sharedPreferences.edit().clear().commit()
        }
    }
}
