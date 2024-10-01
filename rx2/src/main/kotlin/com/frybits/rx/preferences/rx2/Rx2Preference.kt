@file:JvmName("Rx2Preference")

package com.frybits.rx.preferences.rx2

import android.content.SharedPreferences
import androidx.annotation.CheckResult
import com.frybits.rx.preferences.core.Preference
import com.google.common.base.Optional
import io.reactivex.Observable
import io.reactivex.functions.Consumer

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

private const val RX2_STREAM = "rx2-stream"

/**
 * Observe changes to this preference. The current [Preference.value] or [Preference.defaultValue] will be emitted
 * on start of collection.
 */
@CheckResult
fun <T : Any> Preference<T>.asObservable(): Observable<T> {
    return keysChanged.filter { it.orNull() == key || it.orNull() == null }
        .startWith(Optional.absent())
        .map {
            return@map value
        }
}

/**
 * An action which stores a new value for this preference
 */
@CheckResult
fun <T : Any> Preference<T>.asConsumer(): Consumer<T> {
    return Consumer {
        value = it
    }
}

private val <T> Preference<T>.keysChanged: Observable<Optional<String?>>
    get() = rxSharedPreferences.getOrCreateKeyChangedStream(RX2_STREAM) {
        Observable.create<Optional<String?>> { emitter ->
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                check(prefs === rxSharedPreferences.sharedPreferences) { "Rx2SharedPreferences not listening to the right SharedPreferences" }
                emitter.onNext(Optional.fromNullable(key)) // Handle `null` values
            }

            emitter.setCancellable {
                rxSharedPreferences.sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                    listener
                )
            }

            rxSharedPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        }.share()
    }
