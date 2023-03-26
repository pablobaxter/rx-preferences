package com.frybits.rx.preferences.livedata

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.CheckResult
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.frybits.rx.preferences.core.BaseRxSharedPreferences
import com.frybits.rx.preferences.core.BooleanAdapter
import com.frybits.rx.preferences.core.ConverterAdapter
import com.frybits.rx.preferences.core.EnumAdapter
import com.frybits.rx.preferences.core.FloatAdapter
import com.frybits.rx.preferences.core.IntegerAdapter
import com.frybits.rx.preferences.core.LongAdapter
import com.frybits.rx.preferences.core.Preference
import com.frybits.rx.preferences.core.StringAdapter
import com.frybits.rx.preferences.core.StringSetAdapter

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

class LiveDataSharedPreferences @VisibleForTesting internal constructor(
    sharedPreferences: SharedPreferences,
    overrideKeyChanges: LiveData<String?>?
) : BaseRxSharedPreferences(sharedPreferences) {

    companion object {

        /**
         * Creates a [LiveDataSharedPreferences] backed by the [SharedPreferences] passed in
         *
         * @param sharedPreferences The preferences to back this factory
         * @param keyChanges Optional [LiveData] that this factory will use to listen for key changes
         *
         * @return A new instance of [LiveDataSharedPreferences]
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            sharedPreferences: SharedPreferences,
            keyChanges: LiveData<String?>? = null
        ): LiveDataSharedPreferences {
            return LiveDataSharedPreferences(sharedPreferences, keyChanges)
        }
    }

    // Either use the key changes flow passed in, or the default flow
    private val keyChanges: LiveData<String?> =
        overrideKeyChanges ?: SharedPreferenceKeyChangedLiveData(sharedPreferences)

    /** Creates a [Boolean] preference for the [key] with a default of `false` */
    @CheckResult
    fun getBoolean(key: String?): LiveDataPreference<Boolean> {
        return getBoolean(key, false)
    }

    /** Creates a [Boolean] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getBoolean(
        key: String?,
        defaultValue: Boolean
    ): LiveDataPreference<Boolean> {
        return Preference(
            sharedPreferences,
            key,
            defaultValue,
            BooleanAdapter
        ).asLiveDataPreference(keyChanges)
    }

    /** Creates a [T] preference for the [key] using the enum class type [T] with a default of [defaultValue] */
    @CheckResult
    inline fun <reified T : Enum<T>> getEnum(key: String?, defaultValue: T): LiveDataPreference<T> {
        return getEnum(key, defaultValue, T::class.java)
    }

    /** Creates a [T] preference for the [key] using the enum class [clazz] with a default of [defaultValue] */
    @CheckResult
    public override fun <T : Enum<T>> getEnum(
        key: String?,
        defaultValue: T,
        clazz: Class<T>
    ): LiveDataPreference<T> {
        return Preference(
            sharedPreferences,
            key,
            defaultValue,
            EnumAdapter(clazz)
        ).asLiveDataPreference(keyChanges)
    }

    /** Creates a [Float] preference for the [key] with a default of `0F` */
    @CheckResult
    fun getFloat(key: String?): LiveDataPreference<Float> {
        return getFloat(key, 0F)
    }

    /** Creates a [Float] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getFloat(key: String?, defaultValue: Float): LiveDataPreference<Float> {
        return Preference(sharedPreferences, key, defaultValue, FloatAdapter).asLiveDataPreference(
            keyChanges
        )
    }

    /** Creates a [Int] preference for the [key] with a default of `0` */
    @CheckResult
    fun getInteger(key: String?): LiveDataPreference<Int> {
        return getInteger(key, 0)
    }

    /** Creates a [Int] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getInteger(key: String?, defaultValue: Int): LiveDataPreference<Int> {
        return Preference(
            sharedPreferences,
            key,
            defaultValue,
            IntegerAdapter
        ).asLiveDataPreference(keyChanges)
    }

    /** Creates a [Long] preference for the [key] with a default of `0L` */
    @CheckResult
    fun getLong(key: String?): LiveDataPreference<Long> {
        return getLong(key, 0L)
    }

    /** Creates a [Long] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getLong(key: String?, defaultValue: Long): LiveDataPreference<Long> {
        return Preference(sharedPreferences, key, defaultValue, LongAdapter).asLiveDataPreference(
            keyChanges
        )
    }

    /** Creates a [T] preference for the [key] using the [converter], and with a default of [defaultValue] */
    @CheckResult
    public override fun <T> getObject(
        key: String?,
        defaultValue: T,
        converter: Preference.Converter<T>
    ): LiveDataPreference<T> {
        return Preference(
            sharedPreferences,
            key,
            defaultValue,
            ConverterAdapter(converter)
        ).asLiveDataPreference(keyChanges)
    }

    /** Creates a [String] preference for the [key] with a default of `null` */
    @CheckResult
    fun getString(key: String?): LiveDataPreference<String?> {
        return getString(key, null)
    }

    /** Creates a [String] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getString(
        key: String?,
        defaultValue: String?
    ): LiveDataPreference<String?> {
        return Preference(sharedPreferences, key, defaultValue, StringAdapter).asLiveDataPreference(
            keyChanges
        )
    }

    /** Creates a string [Set] preference for the [key] with a default of `null` */
    @CheckResult
    fun getStringSet(key: String?): LiveDataPreference<Set<String?>?> {
        return getStringSet(key, null)
    }

    /** Creates a string [Set] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    public override fun getStringSet(
        key: String?,
        defaultValue: Set<String?>?
    ): LiveDataPreference<Set<String?>?> {
        return Preference(
            sharedPreferences,
            key,
            defaultValue,
            StringSetAdapter
        ).asLiveDataPreference(keyChanges)
    }
}

private class SharedPreferenceKeyChangedLiveData(private val sharedPreferences: SharedPreferences) :
    LiveData<String?>() {

    private val listener = OnSharedPreferenceChangeListener { prefs, key ->
        check(prefs === sharedPreferences) { "CoroutinePreferences not listening to the right SharedPreferences" }
        value = key
    }

    override fun onActive() {
        super.onActive()
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
