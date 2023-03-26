package com.frybits.rx.preferences.livedata

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.frybits.rx.preferences.core.IntegerAdapter
import com.frybits.rx.preferences.core.Preference
import org.junit.Rule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

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

class LiveDataPreferenceTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Test
    fun testPreferenceLiveDataEmitOnStart() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keysChangedLiveData = MutableLiveData<String?>()
        val liveDataPref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asLiveDataPreference(keysChangedLiveData)

        val testResult = arrayListOf<Int>()
        liveDataPref.asLiveData().observeForever { testResult.add(it) }

        // Ensure value is emitted on start
        assertEquals(1, testResult.size)
        assertEquals(2, testResult.first())
        verify(sharedPrefs, times(1)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceLiveDataNoEmit() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keysChangedLiveData = MutableLiveData<String?>()
        val liveDataPref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asLiveDataPreference(keysChangedLiveData)

        val testResult = arrayListOf<Int>()
        liveDataPref.asLiveData().observeForever { testResult.add(it) }

        // No value is emitted for other keys
        keysChangedLiveData.value = "test2"
        assertEquals(1, testResult.size) // OnStart already emitted at least once
        verify(sharedPrefs, times(1)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceLiveDataEmitOnClear() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keysChangedLiveData = MutableLiveData<String?>()
        val liveDataPref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asLiveDataPreference(keysChangedLiveData)

        val testResult = arrayListOf<Int>()
        liveDataPref.asLiveData().observeForever { testResult.add(it) }

        // Value is emitted for "clear" event
        keysChangedLiveData.value = null
        assertEquals(2, testResult.size)
        verify(sharedPrefs, times(2)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceLiveDataOnKeyEmit() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keysChangedLiveData = MutableLiveData<String?>()
        val liveDataPref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asLiveDataPreference(keysChangedLiveData)

        val testResult = arrayListOf<Int>()
        liveDataPref.asLiveData().observeForever { testResult.add(it) }

        // Value is emitted for normal use case
        keysChangedLiveData.value = "test"
        assertEquals(2, testResult.size)
        verify(sharedPrefs, times(2)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceObserver() {
        val editor = mock<SharedPreferences.Editor>()
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val liveDataPref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asLiveDataPreference(MutableLiveData())

        val observer = liveDataPref.asObserver()

        observer.onChanged(1)
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(1))
        verify(editor).apply()
    }
}
