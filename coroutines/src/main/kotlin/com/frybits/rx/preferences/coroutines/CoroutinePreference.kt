/*
 *  Copyright 2023 Pablo Baxter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Created by Pablo Baxter (Github: pablobaxter)
 * https://github.com/pablobaxter/rx-preferences
 */

@file:JvmName("CoroutinePreference")

package com.frybits.rx.preferences.coroutines

import android.content.SharedPreferences
import androidx.annotation.CheckResult
import com.frybits.rx.preferences.core.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import java.io.Closeable

private const val COROUTINE_STREAM = "coroutine-stream"
private const val COROUTINE_SCOPE = "coroutine-scope"

/**
 * Observe changes to this preference. The current [Preference.value] or [Preference.defaultValue] will be emitted
 * on start of collection.
 */
@CheckResult
fun <T> Preference<T>.asFlow(): Flow<T> {
    return keysChanged.filter { it == key || it == null }
        .onStart { emit("") }
        .map { value }
}

/**
 * Saves the given value using [android.content.SharedPreferences.Editor.commit], suspending using [Dispatchers.IO].
 *
 * @return `true` if the data was stored, `false` if there was an error storing the data.
 */
@JvmSynthetic
suspend fun <T> Preference<T>.commitValue(value: T): Boolean {
    return withContext(Dispatchers.IO) {
        val editor = rxSharedPreferences.sharedPreferences.edit()
        adapter.set(key, value, editor)
        return@withContext editor.commit()
    }
}

/**
 * An action which stores a new value for this preference
 *
 * @param committing Flag to indicate that [android.content.SharedPreferences.Editor.commit] should be used instead of [android.content.SharedPreferences.Editor.apply].
 * If set to `true`, emissions to the collector will suspend on [Dispatchers.IO] until the commit is completed or throw an [PreferenceNotStoredException] if it has failed.
 */
@CheckResult
fun <T> Preference<T>.asCollector(committing: Boolean = false): FlowCollector<T> {
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

/**
 * Deletes the underlying value using [android.content.SharedPreferences.Editor.commit], suspending using [Dispatchers.IO].
 */
@JvmSynthetic
suspend fun <T> Preference<T>.deleteAndCommit(): Boolean {
    return withContext(Dispatchers.IO) {
        return@withContext rxSharedPreferences.sharedPreferences.edit().remove(key).commit()
    }
}

private val <T> Preference<T>.keysChanged: Flow<String?>
    get() {
        // Prevents "IllegalStateException: Recursive update" in ConcurrentHashMap
        val scope = rxScope
        return rxSharedPreferences.getOrCreateKeyChangedStream(COROUTINE_STREAM) {
            callbackFlow {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                    check(prefs === rxSharedPreferences.sharedPreferences) { "CoroutinePreferences not listening to the right SharedPreferences" }
                    trySendBlocking(key)
                }

                rxSharedPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

                awaitClose {
                    rxSharedPreferences.sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                        listener
                    )
                }
            }.shareIn(scope, SharingStarted.WhileSubscribed())
        }
    }

private val <T> Preference<T>.rxScope: CoroutineScope
    get() = rxSharedPreferences.getOrCreateKeyChangedStream(COROUTINE_SCOPE) {
        CoroutineCloseable(CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate))
    }.coroutineScope

private class CoroutineCloseable(val coroutineScope: CoroutineScope) : Closeable {

    override fun close() {
        coroutineScope.cancel()
    }
}
