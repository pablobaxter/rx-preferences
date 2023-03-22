package com.frybits.rx.preferences.rx2

import android.content.SharedPreferences
import io.reactivex.subjects.PublishSubject
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
import kotlin.test.assertFailsWith

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

class Rx2SharedPreferencesTest {

    private lateinit var keysChangedPublisher: PublishSubject<Optional<String?>>
    private lateinit var rx2SharedPreferences: Rx2SharedPreferences

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @BeforeTest
    fun setup() {
        MockitoAnnotations.openMocks(this)

        keysChangedPublisher = PublishSubject.create()
        rx2SharedPreferences = Rx2SharedPreferences(sharedPreferences, keysChangedPublisher)
    }

    @AfterTest
    fun tearDown() {
        Mockito.clearAllCaches()
    }

    @Test
    fun testKey() {
        val rxPref = rx2SharedPreferences.getBoolean("test")
        assertEquals("test", rxPref.key)
    }

    @Test
    fun testCoroutinePreferencesDefaults() {
        assertEquals(false, rx2SharedPreferences.getBoolean("test").defaultValue)
        assertEquals(0F, rx2SharedPreferences.getFloat("test").defaultValue)
        assertEquals(0, rx2SharedPreferences.getInteger("test").defaultValue)
        assertEquals(0L, rx2SharedPreferences.getLong("test").defaultValue)
        assertEquals("", rx2SharedPreferences.getString("test").defaultValue)
        assertEquals(emptySet(), rx2SharedPreferences.getStringSet("test").defaultValue)
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
        assertEquals(true, rx2SharedPreferences.getBoolean("test", true).value)
        assertEquals(Roshambo.ROCK, rx2SharedPreferences.getEnum("test", Roshambo.ROCK).value)
        assertEquals(1F, rx2SharedPreferences.getFloat("test", 1F).value)
        assertEquals(1, rx2SharedPreferences.getInteger("test", 1).value)
        assertEquals(1L, rx2SharedPreferences.getLong("test", 1L).value)
        assertEquals("bar", rx2SharedPreferences.getString("test", "bar").value)
        assertEquals(setOf("foo"), rx2SharedPreferences.getStringSet("test", setOf("foo")).value)
        assertEquals(Point(1, 1), rx2SharedPreferences.getObjectNonNull("test", Point(1, 1), spy<PointPreferenceConverter>()).value)
    }

    @Test
    fun testStringNullDefaultValueThrows() {
        assertFailsWith<NullPointerException>("defaultValue == null") { rx2SharedPreferences.getString("key", null) }
    }

    @Test
    fun testStringSetNullDefaultValueThrows() {
        assertFailsWith<NullPointerException>("defaultValue == null") { rx2SharedPreferences.getStringSet("key", null) }
    }
}
