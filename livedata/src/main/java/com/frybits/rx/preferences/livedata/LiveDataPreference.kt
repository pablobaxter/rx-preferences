package com.frybits.rx.preferences.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
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

interface LiveDataPreference<T>: Preference<T> {

    fun asLiveData(): LiveData<T>

    fun asObserver(): Observer<T>
}

// Wraps the underling preference and returns the CoroutinePreference variant.
// Marked as internal, to prevent improper usage of this, as it is possible to continuously wrap the same object forever.
internal fun <T> Preference<T>.asLiveDataPreference(keysChanged: LiveData<String?>): LiveDataPreference<T> = LiveDataPreferenceImpl(this, keysChanged)

private class LiveDataPreferenceImpl<T>(
    private val preference: Preference<T>,
    private val keysChanged: LiveData<String?>
): LiveDataPreference<T>, Preference<T> by preference {

    override fun asLiveData(): LiveData<T> {
        val mediatorLiveData = MediatorLiveData<T>()
        mediatorLiveData.value = value
        mediatorLiveData.addSource(keysChanged) {
            if (it == key || it == null) {
                mediatorLiveData.value = value
            }
        }
        return mediatorLiveData
    }

    override fun asObserver(): Observer<T> {
        return Observer {
            value = it
        }
    }
}
