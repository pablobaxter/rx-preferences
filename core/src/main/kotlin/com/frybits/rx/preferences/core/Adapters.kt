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

package com.frybits.rx.preferences.core

import android.content.SharedPreferences
import com.google.common.base.Optional

/** Stores and retrieves instances of [T] in [SharedPreferences]. */
interface Adapter<T> {

    /**
     * Retrieve the value for [key] from [sharedPreference], or [defaultValue]
     * if the preference is unset, or was set to `null`.
     */
    fun get(key: String?, sharedPreference: SharedPreferences, defaultValue: T): T

    /**
     * Store [value] for [key] in [editor].
     *
     * Note: Implementations **must not** call [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] on
     * [editor]
     */
    fun set(key: String?, value: T, editor: SharedPreferences.Editor)
}

/** Store and retrieves instances of [Boolean] in [SharedPreferences] */
object BooleanAdapter : Adapter<Boolean> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: Boolean
    ): Boolean {
        return sharedPreference.getBoolean(key, defaultValue)
    }

    override fun set(key: String?, value: Boolean, editor: SharedPreferences.Editor) {
        editor.putBoolean(key, value)
    }
}

/** Store and retrieves instances of [T] converted into a [String] using [converter] in [SharedPreferences] */
class ConverterAdapter<T>(private val converter: Preference.Converter<T>) : Adapter<T> {
    override fun get(key: String?, sharedPreference: SharedPreferences, defaultValue: T): T {
        val serialized = sharedPreference.getString(key, null) ?: return defaultValue
        return converter.deserialize(serialized)
    }

    override fun set(key: String?, value: T, editor: SharedPreferences.Editor) {
        val serialized = converter.serialize(value)
        editor.putString(key, serialized)
    }
}

/** Stores and retrieves instances of enum [T] converted into a [String] in [SharedPreferences] */
class EnumAdapter<T : Enum<T>>(private val clazz: Class<T>) : Adapter<T> {

    override fun get(key: String?, sharedPreference: SharedPreferences, defaultValue: T): T {
        val value = sharedPreference.getString(key, null) ?: return defaultValue
        return try {
            java.lang.Enum.valueOf(clazz, value)
        } catch (e: IllegalArgumentException) {
            defaultValue
        }
    }

    override fun set(key: String?, value: T, editor: SharedPreferences.Editor) {
        editor.putString(key, value.name)
    }
}

/** Store and retrieves instances of [Float] in [SharedPreferences] */
object FloatAdapter : Adapter<Float> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: Float
    ): Float {
        return sharedPreference.getFloat(key, defaultValue)
    }

    override fun set(key: String?, value: Float, editor: SharedPreferences.Editor) {
        editor.putFloat(key, value)
    }
}

/** Store and retrieves instances of [Int] in [SharedPreferences] */
object IntegerAdapter : Adapter<Int> {
    override fun get(key: String?, sharedPreference: SharedPreferences, defaultValue: Int): Int {
        return sharedPreference.getInt(key, defaultValue)
    }

    override fun set(key: String?, value: Int, editor: SharedPreferences.Editor) {
        editor.putInt(key, value)
    }
}

/** Store and retrieves instances of [Long] in [SharedPreferences] */
object LongAdapter : Adapter<Long> {
    override fun get(key: String?, sharedPreference: SharedPreferences, defaultValue: Long): Long {
        return sharedPreference.getLong(key, defaultValue)
    }

    override fun set(key: String?, value: Long, editor: SharedPreferences.Editor) {
        editor.putLong(key, value)
    }
}

/** Store and retrieves instances of [String] in [SharedPreferences] */
object StringAdapter : Adapter<String?> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: String?
    ): String? {
        return sharedPreference.getString(key, defaultValue)
    }

    override fun set(key: String?, value: String?, editor: SharedPreferences.Editor) {
        editor.putString(key, value)
    }
}

/** Store and retrieves instances of a collection of [String] within a [Set] in [SharedPreferences] */
object StringSetAdapter : Adapter<Set<String?>?> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: Set<String?>?
    ): Set<String?>? {
        return sharedPreference.getStringSet(key, defaultValue)?.toSet()
    }

    override fun set(key: String?, value: Set<String?>?, editor: SharedPreferences.Editor) {
        editor.putStringSet(key, value)
    }
}

internal class OptionalAdapter<T>(private val adapter: Adapter<T?>) : Adapter<Optional<T>> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: Optional<T>
    ): Optional<T> {
        return Optional.fromNullable(adapter.get(key, sharedPreference, defaultValue.orNull()))
    }

    override fun set(key: String?, value: Optional<T>, editor: SharedPreferences.Editor) {
        adapter.set(key, value.orNull(), editor)
    }
}
