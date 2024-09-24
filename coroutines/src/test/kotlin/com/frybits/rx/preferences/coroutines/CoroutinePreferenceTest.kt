package com.frybits.rx.preferences.coroutines

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.frybits.rx.preferences.core.IntegerAdapter
import com.frybits.rx.preferences.core.Preference
import com.frybits.rx.preferences.core.RxSharedPreferences.Companion.asRxSharedPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

class CoroutinePreferenceTest {

    @Test
    fun testPreferenceFlowOnStart() = runTest {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val coroutinePref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        val testResult = arrayListOf<Int>()
        val job = launch {
            coroutinePref.asFlow().toCollection(testResult)
        }
        delay(1)

        // Ensure value is emitted on start
        assertEquals(1, testResult.size)
        assertEquals(2, testResult.first())
        verify(sharedPrefs, times(1)).getInt(eq("test"), eq(-1))

        job.cancel()
        job.join()
    }

    @Test
    fun testPreferenceFlowNoEmit() = runTest {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedFlow = MutableSharedFlow<String?>()
        val rxSharedPreferences = sharedPrefs.asRxSharedPreferences()
        rxSharedPreferences.getOrCreateKeyChangedStream("coroutine-stream") { keyChangeSharedFlow }
        val coroutinePref = Preference(rxSharedPreferences, "test", -1, IntegerAdapter)

        val testResult = arrayListOf<Int>()
        val job = launch {
            coroutinePref.asFlow().toCollection(testResult)
        }
        delay(1)

        // No value is emitted for other keys
        keyChangeSharedFlow.emit("test2")
        assertEquals(1, testResult.size) // OnStart already emitted at least once
        verify(sharedPrefs, times(1)).getInt(eq("test"), eq(-1))

        job.cancel()
        job.join()
    }

    @Test
    fun testPreferenceFlowEmitOnClear() = runTest {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedFlow = MutableSharedFlow<String?>()
        val rxSharedPreferences = sharedPrefs.asRxSharedPreferences()
        rxSharedPreferences.getOrCreateKeyChangedStream("coroutine-stream") { keyChangeSharedFlow }
        val coroutinePref = Preference(rxSharedPreferences, "test", -1, IntegerAdapter)

        val testResult = arrayListOf<Int>()
        val job = launch {
            coroutinePref.asFlow().toCollection(testResult)
        }
        delay(1)

        // Value is emitted for "clear" event
        keyChangeSharedFlow.emit(null)
        assertEquals(2, testResult.size)
        verify(sharedPrefs, times(2)).getInt(eq("test"), eq(-1))

        job.cancel()
        job.join()
    }

    @Test
    fun testPreferenceFlowOnKeyEmit() = runTest {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedFlow = MutableSharedFlow<String?>()
        val rxSharedPreferences = sharedPrefs.asRxSharedPreferences()
        rxSharedPreferences.getOrCreateKeyChangedStream("coroutine-stream") { keyChangeSharedFlow }
        val coroutinePref = Preference(rxSharedPreferences, "test", -1, IntegerAdapter)

        val testResult = arrayListOf<Int>()
        val job = launch {
            coroutinePref.asFlow().toCollection(testResult)
        }
        delay(1)

        // Value is emitted for normal use case
        keyChangeSharedFlow.emit("test")
        assertEquals(2, testResult.size)
        verify(sharedPrefs, times(2)).getInt(eq("test"), eq(-1))

        job.cancel()
        job.join()
    }

    @Test
    fun testPreferenceCollectorWithFalse() = runTest {
        val editor = mock<Editor>()
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val coroutinePref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        val collector = coroutinePref.asCollector()

        collector.emit(1)
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(1))
        verify(editor).apply()
    }

    @Test
    fun testPreferenceCollectorWithTrueFailing() = runTest {
        val editor = mock<Editor>()
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val coroutinePref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        val collector = coroutinePref.asCollector(committing = true)

        assertFailsWith<PreferenceNotStoredException> { collector.emit(1) }
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(1))
        verify(editor).commit()
    }

    @Test
    fun testPreferenceCollectorWithTrue() = runTest {
        val editor = mock<Editor> {
            on { commit() } doReturn true
        }
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val coroutinePref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        val collector = coroutinePref.asCollector(committing = true)

        collector.emit(1)
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(1))
        verify(editor).commit()
    }

    @Test
    fun testCommitValue() = runTest {
        val editor = mock<Editor> {
            on { commit() } doReturn true
        }
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val coroutinePref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        assertTrue { coroutinePref.commitValue(2) }
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(2))
        verify(editor).commit()
    }

    @Test
    fun testDeleteAndCommit() = runTest {
        val editor = mock<Editor> {
            on { remove(any()) } doReturn mock
            on { commit() } doReturn true
        }
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val coroutinePref = Preference(sharedPrefs.asRxSharedPreferences(), "test", -1, IntegerAdapter)

        assertTrue { coroutinePref.deleteAndCommit() }
        verify(sharedPrefs).edit()
        verify(editor).remove(eq("test"))
        verify(editor).commit()
    }
}
