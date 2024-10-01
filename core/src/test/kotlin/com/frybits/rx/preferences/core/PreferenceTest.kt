package com.frybits.rx.preferences.core

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.frybits.rx.preferences.core.RxSharedPreferences.Companion.asRxSharedPreferences
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
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

class PreferenceTest {

    @Test
    fun testPreferenceGet() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 0
        }
        val pref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        assertEquals(0, pref.value, "SharedPreferences not reached")
        verify(sharedPrefs).getInt(eq("test"), eq(-1))
        verifyNoMoreInteractions(sharedPrefs)
    }

    @Test
    fun testPreferenceSet() {
        val editor = mock<Editor>()
        val sharedPrefs = mock<SharedPreferences> {
            on { contains(any()) } doReturn true
            on { edit() } doReturn editor
        }
        val pref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        pref.value = 2
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(2))
        verify(editor).apply()
        verifyNoMoreInteractions(editor)
        verifyNoMoreInteractions(sharedPrefs)
    }

    @Test
    fun testIsSet() {
        val sharedPrefs = mock<SharedPreferences> {
            on { contains(any()) } doReturn true
        }
        val pref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        assertEquals(true, pref.isSet, "SharedPreferences not reached")
        verify(sharedPrefs).contains(eq("test"))
        verifyNoMoreInteractions(sharedPrefs)
    }

    @Test
    fun testDelete() {
        val editor = mock<Editor> {
            on { remove(any()) } doReturn mock
        }
        val sharedPrefs = mock<SharedPreferences> {
            on { contains(any()) } doReturn true
            on { edit() } doReturn editor
        }
        val pref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        pref.delete()
        verify(sharedPrefs).edit()
        verify(editor).remove(eq("test"))
        verify(editor).apply()
        verifyNoMoreInteractions(editor)
        verifyNoMoreInteractions(sharedPrefs)
    }
}
