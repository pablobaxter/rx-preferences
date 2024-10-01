@file:JvmName("PreferenceUtil")

package com.frybits.rx.preferences.core

import com.google.common.base.Optional

/**
 * Converts a preference of a nullable type to be an [Optional] of that same type instead.
 */
fun <T> Preference<T?>.asOptional(): Preference<Optional<T>> {
    return Preference(rxSharedPreferences, key, Optional.fromNullable(defaultValue), OptionalAdapter(adapter))
}
