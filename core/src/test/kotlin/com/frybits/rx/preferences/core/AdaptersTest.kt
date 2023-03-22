package com.frybits.rx.preferences.core

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
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

class AdaptersTest {

    @Test
    fun booleanAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getBoolean(any(), any()) } doReturn true
        }

        assertEquals(true, BooleanAdapter.get("test", sharedPreferences, false), "SharedPreferences not reached")
        verify(sharedPreferences).getBoolean(eq("test"), eq(false))

        BooleanAdapter.set("test", false, editor)
        verify(editor).putBoolean(eq("test"), eq(false))
    }

    @Test
    fun converterAdapterTest() {
        val converter = spy<PointPreferenceConverter>()
        val pointSerialized = converter.serialize(Point(1, 1))
        clearInvocations(converter)
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getString(any(), anyOrNull()) } doReturn pointSerialized
        }

        val adapter = ConverterAdapter(converter)

        assertEquals(Point(1, 1), adapter.get("test", sharedPreferences, Point(0, 0)), "SharedPreferences not reached")
        verify(sharedPreferences).getString(eq("test"), isNull())
        verify(converter).deserialize(eq(pointSerialized))

        adapter.set("test", Point(1, 1), editor)
        verify(converter).serialize(eq(Point(1, 1)))
        verify(editor).putString(eq("test"), eq(pointSerialized))
    }

    @Test
    fun enumAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getString(any(), anyOrNull()) } doReturn Roshambo.ROCK.name
        }

        val adapter = EnumAdapter(Roshambo::class.java)

        assertEquals(Roshambo.ROCK, adapter.get("test", sharedPreferences, Roshambo.SCISSORS), "SharedPreferences not reached")
        verify(sharedPreferences).getString(eq("test"), isNull())

        adapter.set("test", Roshambo.PAPER, editor)
        verify(editor).putString(eq("test"), eq(Roshambo.PAPER.name))
    }

    @Test
    fun floatAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getFloat(any(), any()) } doReturn 1F
        }

        assertEquals(1F, FloatAdapter.get("test", sharedPreferences, 0F), "SharedPreferences not reached")
        verify(sharedPreferences).getFloat(eq("test"), eq(0F))

        FloatAdapter.set("test", 2F, editor)
        verify(editor).putFloat(eq("test"), eq(2F))
    }

    @Test
    fun integerAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 1
        }

        assertEquals(1, IntegerAdapter.get("test", sharedPreferences, 0), "SharedPreferences not reached")
        verify(sharedPreferences).getInt(eq("test"), eq(0))

        IntegerAdapter.set("test", 2, editor)
        verify(editor).putInt(eq("test"), eq(2))
    }

    @Test
    fun longAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getLong(any(), any()) } doReturn 1L
        }

        assertEquals(1L, LongAdapter.get("test", sharedPreferences, 0L), "SharedPreferences not reached")
        verify(sharedPreferences).getLong(eq("test"), eq(0L))

        LongAdapter.set("test", 2L, editor)
        verify(editor).putLong(eq("test"), eq(2L))
    }

    @Test
    fun stringAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getString(any(), anyOrNull()) } doReturn "testString"
        }

        assertEquals("testString", StringAdapter.get("test", sharedPreferences, null), "SharedPreferences not reached")
        verify(sharedPreferences).getString(eq("test"), isNull())

        StringAdapter.set("test", "test2", editor)
        verify(editor).putString(eq("test"), eq("test2"))
    }

    @Test
    fun stringSetAdapterTest() {
        val editor = mock<Editor>()
        val sharedPreferences = mock<SharedPreferences> {
            on { getStringSet(any(), anyOrNull()) } doReturn setOf("test")
        }

        assertEquals(setOf("test"), StringSetAdapter.get("test", sharedPreferences, null), "SharedPreferences not reached")
        verify(sharedPreferences).getStringSet(eq("test"), isNull())

        StringSetAdapter.set("test", emptySet(), editor)
        verify(editor).putStringSet(eq("test"), eq(emptySet()))
    }
}
