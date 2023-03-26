package com.frybits.rx.preferences.rx3

import android.content.SharedPreferences
import com.frybits.rx.preferences.core.Adapter
import com.frybits.rx.preferences.core.Preference

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

// Internal adapter for RxJava to ensure parity with original Legacy function to support transition from https://github.com/f2prateek/rx-preferences/
internal class Rx3ConverterAdapter<T : Any>(private val converter: Preference.Converter<T>) :
    Adapter<T> {
    override fun get(key: String?, sharedPreference: SharedPreferences, defaultValue: T): T {
        val serialized = sharedPreference.getString(key, null) ?: return defaultValue
        return requireNotNull(converter.deserialize(serialized)) { throw NullPointerException("Deserialized value must not be null from string: $serialized") }
    }

    override fun set(key: String?, value: T, editor: SharedPreferences.Editor) {
        val serialized =
            requireNotNull(converter.serialize(value)) { throw NullPointerException("Serialized string must not be null from value: $value") }
        editor.putString(key, serialized)
    }
}

/** Store and retrieves instances of [String] in [SharedPreferences] */
internal object NonNullStringAdapter : Adapter<String> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: String
    ): String {
        return sharedPreference.getString(key, defaultValue) ?: defaultValue
    }

    override fun set(key: String?, value: String, editor: SharedPreferences.Editor) {
        editor.putString(key, value)
    }
}

/** Store and retrieves instances of a collection of [String] within a [Set] in [SharedPreferences] */
internal object NonNullStringSetAdapter : Adapter<Set<String?>> {
    override fun get(
        key: String?,
        sharedPreference: SharedPreferences,
        defaultValue: Set<String?>
    ): Set<String?> {
        return sharedPreference.getStringSet(key, defaultValue)?.toSet() ?: defaultValue
    }

    override fun set(key: String?, value: Set<String?>, editor: SharedPreferences.Editor) {
        editor.putStringSet(key, value)
    }
}
