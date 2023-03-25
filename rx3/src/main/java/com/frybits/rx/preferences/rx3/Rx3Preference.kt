package com.frybits.rx.preferences.rx3

import androidx.annotation.CheckResult
import com.frybits.rx.preferences.core.Preference
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

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

/**
 * A preference of type [T] that exposes RxJava2 [Observable] as the reactive framework. Instances are created from [Rx3SharedPreferences] factory.
 *
 * This preference exposes RxJava2 specific functions.
 */
interface Rx3Preference<T: Any>: Preference<T> {

    /**
     * Legacy function to support transition from [f2prateek/rx-preference](https://github.com/f2prateek/rx-preferences/blob/master/rx-preferences/src/main/java/com/f2prateek/rx/preferences2/Preference.java).
     *
     * @see [Preference.key]
     */
    @Deprecated(message = "Use `key` instead.", replaceWith = ReplaceWith("key"), level = DeprecationLevel.ERROR)
    fun key(): String? {
        return key
    }

    /**
     * Legacy function to support transition from [f2prateek/rx-preference](https://github.com/f2prateek/rx-preferences/blob/master/rx-preferences/src/main/java/com/f2prateek/rx/preferences2/Preference.java).
     *
     * @see [Preference.defaultValue]
     */
    @Deprecated(message = "Use `defaultValue` instead.", replaceWith = ReplaceWith("defaultValue"), level = DeprecationLevel.ERROR)
    fun defaultValue(): T {
        return defaultValue
    }

    /**
     * Legacy function to support transition from [f2prateek/rx-preference](https://github.com/f2prateek/rx-preferences/blob/master/rx-preferences/src/main/java/com/f2prateek/rx/preferences2/Preference.java).
     *
     * @see [Preference.value]
     */
    @Deprecated(message = "Use `value` instead.", replaceWith = ReplaceWith("value"), level = DeprecationLevel.ERROR)
    fun get(): T {
        return value
    }

    /**
     * Legacy function to support transition from [f2prateek/rx-preference](https://github.com/f2prateek/rx-preferences/blob/master/rx-preferences/src/main/java/com/f2prateek/rx/preferences2/Preference.java).
     *
     * @see [Preference.value]
     */
    // Unable to use ReplaceWith, due to bug with setters. https://youtrack.jetbrains.com/issue/KTIJ-12836/ReplaceWith-cannot-replace-function-invocation-with-property-assignment
    @Deprecated(message = "Use `this.value = value` instead.", level = DeprecationLevel.ERROR)
    fun set(value: T)

    /**
     * Observe changes to this preference. The current [value] or [defaultValue] will be emitted
     * on start of collection.
     */
    @CheckResult
    fun asObservable(): Observable<T>

    /**
     * An action which stores a new value for this preference
     */
    @CheckResult
    fun asConsumer(): Consumer<in T>
}

// Wraps the underling preference and returns the Rx3Preference variant.
// Marked as internal, to prevent improper usage of this, as it is possible to continuously wrap the same object forever.
internal fun <T: Any> Preference<T>.asRx3Preference(keysChanged: Observable<Optional<String?>>): Rx3Preference<T> = Rx3PreferenceImpl(this, keysChanged)

private class Rx3PreferenceImpl<T: Any>(
    private val preference: Preference<T>,
    private val keysChanged: Observable<Optional<String?>>
): Rx3Preference<T>, Preference<T> by preference {

    @Deprecated("Use `this.value = value` instead.", level = DeprecationLevel.ERROR)
    override fun set(value: T) {
        this.value = value
    }

    override var value: T
        get() = preference.value
        set(value) {
            preference.value = value
        }

    override fun asObservable(): Observable<T> {
        return keysChanged.filter { it.value == key || it.value == null }
            .startWithItem(Optional(""))
            .map { value }
    }

    override fun asConsumer(): Consumer<in T> {
        return Consumer {
            value = it
        }
    }
}
