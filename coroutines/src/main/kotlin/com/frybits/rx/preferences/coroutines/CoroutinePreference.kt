package com.frybits.rx.preferences.coroutines

import androidx.annotation.CheckResult
import com.frybits.rx.preferences.core.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
 * A preference of type [T] that exposes Kotlin [Flow] as the reactive framework. Instances are created from [CoroutineSharedPreferences] factory.
 *
 * This preference exposes Coroutine specific functions.
 */
interface CoroutinePreference<T> : Preference<T> {

    /**
     * Observe changes to this preference. The current [value] or [defaultValue] will be emitted
     * on start of collection.
     */
    @CheckResult
    fun asFlow(): Flow<T>

    /**
     * An action which stores a new value for this preference
     *
     * @param committing Flag to indicate that [android.content.SharedPreferences.Editor.commit] should be used instead.
     * If set to `true`, emissions to the collector will suspend on [Dispatchers.IO] until the commit is completed or throw an [PreferenceNotStoredException] if it has failed.
     */
    @CheckResult
    fun asCollector(committing: Boolean = false): FlowCollector<T>

    /**
     * Saves the given value using [android.content.SharedPreferences.Editor.commit], suspending using [Dispatchers.IO].
     *
     * @return `true` if the data was stored, `false` if there was an error storing the data.
     */
    @JvmSynthetic
    suspend fun commitValue(value: T): Boolean

    /**
     * Deletes the underlying value using [android.content.SharedPreferences.Editor.commit], suspending using [Dispatchers.IO].
     */
    @JvmSynthetic
    suspend fun deleteAndCommit(): Boolean
}

// Wraps the underling preference and returns the CoroutinePreference variant.
// Marked as internal, to prevent improper usage of this, as it is possible to continuously wrap the same object forever.
@JvmSynthetic
internal fun <T> Preference<T>.asCoroutinePreference(keysChanged: Flow<String?>): CoroutinePreference<T> =
    CoroutinePreferenceImpl(this, keysChanged)

private class CoroutinePreferenceImpl<T>(
    private val preference: Preference<T>,
    private val keysChanged: Flow<String?>
) : CoroutinePreference<T>, Preference<T> by preference {

    override fun asFlow(): Flow<T> {
        return keysChanged.filter { it == key || it == null }
            .onStart { emit("") }
            .map { value }
    }

    override suspend fun commitValue(value: T): Boolean {
        return withContext(Dispatchers.IO) {
            val editor = sharedPreferences.edit()
            adapter.set(key, value, editor)
            return@withContext editor.commit()
        }
    }

    override suspend fun deleteAndCommit(): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext sharedPreferences.edit().remove(key).commit()
        }
    }

    override fun asCollector(committing: Boolean): FlowCollector<T> {
        return FlowCollector { value ->
            if (committing) {
                if (!commitValue(value)) {
                    throw PreferenceNotStoredException(value)
                }
            } else {
                this.value = value
            }
        }
    }
}
