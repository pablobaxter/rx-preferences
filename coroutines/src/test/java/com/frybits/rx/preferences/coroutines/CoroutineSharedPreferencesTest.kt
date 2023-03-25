package com.frybits.rx.preferences.coroutines

import android.content.SharedPreferences
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
import kotlin.test.fail

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

class CoroutineSharedPreferencesTest {

    private lateinit var testScope: TestScope
    private lateinit var keysChangedSharedFlow: MutableSharedFlow<String?>
    private lateinit var coroutineSharedPreferences: CoroutineSharedPreferences

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @BeforeTest
    fun setup() {
        MockitoAnnotations.openMocks(this)

        testScope = TestScope()
        keysChangedSharedFlow = MutableSharedFlow()
        coroutineSharedPreferences = CoroutineSharedPreferences(sharedPreferences, keysChangedSharedFlow, testScope)
    }

    @AfterTest
    fun tearDown() {
        testScope.advanceUntilIdle()
        testScope.cancel()
        Mockito.clearAllCaches()
    }

    @Test
    fun testKey() = testScope.runTest {
        val coroutinePreference = coroutineSharedPreferences.getBoolean("test")
        assertEquals("test", coroutinePreference.key)
    }

    @Test
    fun testCoroutinePreferencesDefaults() = testScope.runTest {
        assertEquals(false, coroutineSharedPreferences.getBoolean("test").defaultValue)
        assertEquals(0F, coroutineSharedPreferences.getFloat("test").defaultValue)
        assertEquals(0, coroutineSharedPreferences.getInteger("test").defaultValue)
        assertEquals(0L, coroutineSharedPreferences.getLong("test").defaultValue)
        assertNull(coroutineSharedPreferences.getString("test").defaultValue)
        assertNull(coroutineSharedPreferences.getStringSet("test").defaultValue)
    }

    @Test
    fun testWithNoValueReturnsDefaultValue() = testScope.runTest {
        sharedPreferences.stub {
            on { getBoolean(any(), any()) } doAnswer { it.getArgument(1) }
            on { getString(any(), anyOrNull()) } doAnswer { it.getArgument(1) }
            on { getFloat(any(), any()) } doAnswer { it.getArgument(1) }
            on { getInt(any(), any()) } doAnswer { it.getArgument(1) }
            on { getLong(any(), any()) } doAnswer { it.getArgument(1) }
            on { getStringSet(any(), anyOrNull()) } doAnswer { it.getArgument(1) }
        }
        assertEquals(true, coroutineSharedPreferences.getBoolean("test", true).value)
        assertEquals(Roshambo.ROCK, coroutineSharedPreferences.getEnum("test", Roshambo.ROCK).value)
        assertEquals(1F, coroutineSharedPreferences.getFloat("test", 1F).value)
        assertEquals(1, coroutineSharedPreferences.getInteger("test", 1).value)
        assertEquals(1L, coroutineSharedPreferences.getLong("test", 1L).value)
        assertEquals("bar", coroutineSharedPreferences.getString("test", "bar").value)
        assertEquals(setOf("foo"), coroutineSharedPreferences.getStringSet("test", setOf("foo")).value)
        assertEquals(Point(1, 1), coroutineSharedPreferences.getObject("test", Point(1, 1), spy<PointPreferenceConverter>()).value)
    }
}
