/*
 *  Copyright 2024 Pablo Baxter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Created by Pablo Baxter (Github: pablobaxter)
 * https://github.com/pablobaxter/rx-preferences
 */

@file:JvmName("PreferenceUtil")

package com.frybits.rx.preferences.core

import com.google.common.base.Optional

/**
 * Converts a preference of a nullable type to be an [Optional] of that same type instead.
 */
fun <T> Preference<T?>.asOptional(): Preference<Optional<T>> {
    return Preference(rxSharedPreferences, key, Optional.fromNullable(defaultValue), OptionalAdapter(adapter))
}
