package com.frybits.rx.preferences.core

import android.content.SharedPreferences
import androidx.annotation.CheckResult

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
 * Abstract factory to create [Preference] objects used reactive frameworks
 *
 * @param sharedPreferences The preferences that back this factory
 */
abstract class BaseRxSharedPreferences(protected val sharedPreferences: SharedPreferences) {

    /** Creates a [Boolean] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun getBoolean(key: String?, defaultValue: Boolean): Preference<Boolean>

    /** Creates a [T] preference for the [key] using the enum class [clazz] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun <T: Enum<T>> getEnum(key: String?, defaultValue: T, clazz: Class<T>): Preference<T>

    /** Creates a [Float] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun getFloat(key: String?, defaultValue: Float): Preference<Float>

    /** Creates a [Int] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun getInteger(key: String?, defaultValue: Int): Preference<Int>

    /** Creates a [Long] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun getLong(key: String?, defaultValue: Long): Preference<Long>

    /** Creates a [T] preference for the [key] using the [converter], and with a default of [defaultValue] */
    @CheckResult
    protected open fun <T> getObject(key: String?, defaultValue: T, converter: Preference.Converter<T>): Preference<T> {
        throw IllegalStateException("Unused function")
    }

    /** Creates a [T] preference for the [key] using the [converter], and with a default of [defaultValue]. This function ensures objects in stream are not `null`. */
    @CheckResult
    protected open fun <T: Any> getObjectNonNull(key: String?, defaultValue: T, converter: Preference.Converter<T>): Preference<T> {
        throw IllegalStateException("Unused function")
    }

    /** Creates a [String] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun getString(key: String?, defaultValue: String?): Preference<out String?>

    /** Creates a string [Set] preference for the [key] with a default of [defaultValue] */
    @CheckResult
    protected abstract fun getStringSet(key: String?, defaultValue: Set<String?>?): Preference<out Set<String?>?>

    /** Clears the underlying shared preferences */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
