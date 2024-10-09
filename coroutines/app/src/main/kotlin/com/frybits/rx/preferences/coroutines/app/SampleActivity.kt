/*
 *  Copyright 2023 Pablo Baxter
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

package com.frybits.rx.preferences.coroutines.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.frybits.rx.preferences.core.Preference
import com.frybits.rx.preferences.core.RxSharedPreferences.Companion.asRxSharedPreferences
import com.frybits.rx.preferences.coroutines.app.databinding.SampleLayoutBinding
import com.frybits.rx.preferences.coroutines.asCollector
import com.frybits.rx.preferences.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SampleActivity : AppCompatActivity() {

    private lateinit var binding: SampleLayoutBinding

    private lateinit var fooBool: Preference<Boolean>
    private lateinit var fooString: Preference<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SampleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val coroutinesPreferences =
            getSharedPreferences("coroutines", MODE_PRIVATE).asRxSharedPreferences()

        fooBool = coroutinesPreferences.getBoolean("fooBool")
        fooString = coroutinesPreferences.getString("fooString")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                bindPreference(binding.checkBox, fooBool)
                bindPreference(binding.checkBox2, fooBool)
                bindPreference(binding.text1, fooString)
                bindPreference(binding.text2, fooString)
            }
        }
    }

    private fun CoroutineScope.bindPreference(
        checkBox: CheckBox,
        preference: Preference<Boolean>
    ) {
        // Bind the preference to the checkbox.
        preference.asFlow()
            .onEach { checkBox.isChecked = it }
            .launchIn(this)

        // Bind the checkbox to the preference.
        launch {
            // Let the collector know to not use "commit"
            val collector = preference.asCollector(committing = false)

            callbackFlow {
                val listener = OnCheckedChangeListener { _, isChecked ->
                    trySend(isChecked)
                }
                checkBox.setOnCheckedChangeListener(listener)
                awaitClose { checkBox.setOnCheckedChangeListener(null) }
            }.collect(collector)
        }
    }

    private fun CoroutineScope.bindPreference(
        editText: EditText,
        preference: Preference<String?>
    ) {
        preference.asFlow()
            .filter { !editText.isFocused }
            .onEach(editText::setText)
            .launchIn(this)

        launch {
            // Let the collector know to not use "commit"
            val collector = preference.asCollector(false)

            callbackFlow {
                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        trySend(s?.toString())
                    }

                    override fun afterTextChanged(s: Editable?) = Unit

                }
                editText.addTextChangedListener(textWatcher)
                awaitClose { editText.removeTextChangedListener(textWatcher) }
            }.collect(collector)
        }
    }
}
