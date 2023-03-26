package com.frybits.rx.preferences.livedata

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.spy
import org.mockito.kotlin.stub
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

class LiveDataSharedPreferencesTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private lateinit var keysChangedLiveData: MutableLiveData<String?>
    private lateinit var liveDataSharedPreferences: LiveDataSharedPreferences

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @BeforeTest
    fun setup() {
        MockitoAnnotations.openMocks(this)

        keysChangedLiveData = MutableLiveData()
        liveDataSharedPreferences = LiveDataSharedPreferences(sharedPreferences, keysChangedLiveData)
    }

    @AfterTest
    fun tearDown() {
        Mockito.clearAllCaches()
    }

    @Test
    fun testKey() {
        val liveDataPreference = liveDataSharedPreferences.getBoolean("test")
        assertEquals("test", liveDataPreference.key)
    }

    @Test
    fun testCoroutinePreferencesDefaults() {
        assertEquals(false, liveDataSharedPreferences.getBoolean("test").defaultValue)
        assertEquals(0F, liveDataSharedPreferences.getFloat("test").defaultValue)
        assertEquals(0, liveDataSharedPreferences.getInteger("test").defaultValue)
        assertEquals(0L, liveDataSharedPreferences.getLong("test").defaultValue)
        assertNull(liveDataSharedPreferences.getString("test").defaultValue)
        assertNull(liveDataSharedPreferences.getStringSet("test").defaultValue)
    }


    @Test
    fun testWithNoValueReturnsDefaultValue() {
        sharedPreferences.stub {
            on { getBoolean(any(), any()) } doAnswer { it.getArgument(1) }
            on { getString(any(), anyOrNull()) } doAnswer { it.getArgument(1) }
            on { getFloat(any(), any()) } doAnswer { it.getArgument(1) }
            on { getInt(any(), any()) } doAnswer { it.getArgument(1) }
            on { getLong(any(), any()) } doAnswer { it.getArgument(1) }
            on { getStringSet(any(), anyOrNull()) } doAnswer { it.getArgument(1) }
        }
        assertEquals(true, liveDataSharedPreferences.getBoolean("test", true).value)
        assertEquals(Roshambo.ROCK, liveDataSharedPreferences.getEnum("test", Roshambo.ROCK).value)
        assertEquals(1F, liveDataSharedPreferences.getFloat("test", 1F).value)
        assertEquals(1, liveDataSharedPreferences.getInteger("test", 1).value)
        assertEquals(1L, liveDataSharedPreferences.getLong("test", 1L).value)
        assertEquals("bar", liveDataSharedPreferences.getString("test", "bar").value)
        assertEquals(setOf("foo"), liveDataSharedPreferences.getStringSet("test", setOf("foo")).value)
        assertEquals(Point(1, 1), liveDataSharedPreferences.getObject("test", Point(1, 1), spy<PointPreferenceConverter>()).value)
    }
}
