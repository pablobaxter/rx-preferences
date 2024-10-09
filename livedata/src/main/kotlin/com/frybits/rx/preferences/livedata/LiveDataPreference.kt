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

@file:JvmName("LiveDataPreference")

package com.frybits.rx.preferences.livedata

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.frybits.rx.preferences.core.Preference

private const val LIVEDATA_STREAM = "livedata-stream"

/**
 * Observe changes to this preference. The current [Preference.value] or [Preference.defaultValue] will be emitted
 * on start of observation.
 */
fun <T> Preference<T>.asLiveData(): LiveData<T> {
    val mediatorLiveData = MediatorLiveData<T>()
    mediatorLiveData.value = value
    mediatorLiveData.addSource(keysChanged) {
        if (it == key || it == null) {
            mediatorLiveData.value = value
        }
    }
    return mediatorLiveData
}

/**
 * An action which stores a new value for this preference
 */
fun <T> Preference<T>.asObserver(): Observer<T> {
    return Observer {
        value = it
    }
}

private val <T> Preference<T>.keysChanged: LiveData<String?>
    get() = rxSharedPreferences.getOrCreateKeyChangedStream(LIVEDATA_STREAM) {
        SharedPreferenceKeyChangedLiveData(rxSharedPreferences.sharedPreferences)
    }

private class SharedPreferenceKeyChangedLiveData(
    private val sharedPreferences: SharedPreferences
) : LiveData<String?>() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        check(prefs === sharedPreferences) { "LiveDataPreference not listening to the right SharedPreferences" }
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
