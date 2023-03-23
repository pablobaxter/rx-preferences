package com.frybits.rx.preferences.rx2

import android.content.SharedPreferences
import com.frybits.rx.preferences.core.IntegerAdapter
import com.frybits.rx.preferences.core.Preference
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.Test
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

class Rx2PreferenceTest {

    @Test
    fun rxConverterMayNotReturnNull() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getString(any(), anyOrNull()) } doReturn "1,2"
        }
        val keyChangeSharedObservable = Observable.empty<Optional<String?>>()
        val rx2Pref = Preference(sharedPrefs, "test", Point(0, 0), Rx2ConverterAdapter(object : Preference.Converter<Point> {
            override fun deserialize(serialized: String?): Point {
                throw NullPointerException()
            }

            override fun serialize(value: Point): String? {
                return null
            }
        })).asRx2Preference(keyChangeSharedObservable)

        assertFailsWith<NullPointerException>("Deserialized value must not be null from string: 1,2") { rx2Pref.value }
        assertFailsWith<NullPointerException>("Serialized string must not be null from value: Point(x=1, y=2)") { rx2Pref.value = Point(1, 2) }
    }

    @Test
    fun testPreferenceObservableStartWith() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedObservable = PublishSubject.create<Optional<String?>>()
        val rx2Pref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asRx2Preference(keyChangeSharedObservable)

        rx2Pref.asObservable().test()
            .assertValueCount(1)
            .assertValue(2)
        verify(sharedPrefs, times(1)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceObservableNoEmit() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedObservable = PublishSubject.create<Optional<String?>>()
        val rx2Pref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asRx2Preference(keyChangeSharedObservable)

        val testObservable = rx2Pref.asObservable().test()

        // No value is emitted for other keys
        keyChangeSharedObservable.onNext("test2".asOptional())
        testObservable.assertValueCount(1) // WithStart already emitted at least once
        verify(sharedPrefs, times(1)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceObservableEmitOnClear() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedObservable = PublishSubject.create<Optional<String?>>()
        val rx2Pref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asRx2Preference(keyChangeSharedObservable)

        val testObservable = rx2Pref.asObservable().test()

        // Value is emitted for "clear" event
        keyChangeSharedObservable.onNext(Optional(null))
        testObservable.assertValueCount(2)
        verify(sharedPrefs, times(2)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceObservableOnKeyEmit() {
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
        }
        val keyChangeSharedObservable = PublishSubject.create<Optional<String?>>()
        val rx2Pref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asRx2Preference(keyChangeSharedObservable)

        val testObservable = rx2Pref.asObservable().test()

        // Value is emitted for normal use case
        keyChangeSharedObservable.onNext("test".asOptional())
        testObservable.assertValueCount(2)
        verify(sharedPrefs, times(2)).getInt(eq("test"), eq(-1))
    }

    @Test
    fun testPreferenceConsumer() {
        val editor = mock<SharedPreferences.Editor>()
        val sharedPrefs = mock<SharedPreferences> {
            on { getInt(any(), any()) } doReturn 2
            on { edit() } doReturn editor
        }
        val rx2Pref = Preference(sharedPrefs, "test", -1, IntegerAdapter).asRx2Preference(Observable.empty())

        val consumer = rx2Pref.asConsumer()

        consumer.accept(1)
        verify(sharedPrefs).edit()
        verify(editor).putInt(eq("test"), eq(1))
        verify(editor).apply()
    }
}
