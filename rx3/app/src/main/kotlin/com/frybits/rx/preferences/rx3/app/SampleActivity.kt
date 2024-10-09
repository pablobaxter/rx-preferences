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

package com.frybits.rx.preferences.rx3.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.frybits.rx.preferences.core.Preference
import com.frybits.rx.preferences.core.RxSharedPreferences.Companion.asRxSharedPreferences
import com.frybits.rx.preferences.core.asOptional
import com.frybits.rx.preferences.rx3.app.databinding.SampleLayoutBinding
import com.frybits.rx.preferences.rx3.asConsumer
import com.frybits.rx.preferences.rx3.asObservable
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class SampleActivity : AppCompatActivity() {

    private lateinit var binding: SampleLayoutBinding

    private lateinit var fooBool: Preference<Boolean>
    private lateinit var fooString: Preference<Optional<String?>>

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SampleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rx3Preferences =
            getSharedPreferences("rx3", MODE_PRIVATE).asRxSharedPreferences()

        fooBool = rx3Preferences.getBoolean("fooBool")
        fooString = rx3Preferences.getString("fooString").asOptional()

        bindPreference(binding.checkBox, fooBool)
        bindPreference(binding.checkBox2, fooBool)
        bindPreference(binding.text1, fooString)
        bindPreference(binding.text2, fooString)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    private fun bindPreference(checkBox: CheckBox, preference: Preference<Boolean>) {
        // Bind the preference to the checkbox.
        disposables.add(
            preference.asObservable()
                .subscribe { checkBox.isChecked = it.or(false) }
        )

        // Bind the checkbox to the preference.
        val consumer = preference.asConsumer()
        disposables.add(
            Observable.create { emitter ->
                val listener = OnCheckedChangeListener { _, isChecked ->
                    emitter.onNext(isChecked)
                }
                checkBox.setOnCheckedChangeListener(listener)
                emitter.setCancellable {
                    checkBox.setOnCheckedChangeListener(null)
                }
            }.subscribe(consumer)
        )
    }

    private fun bindPreference(editText: EditText, preference: Preference<Optional<String?>>) {
        disposables.add(
            preference.asObservable()
                .filter { !editText.isFocused }
                .subscribe { editText.setText(it.orNull()) }
        )

        val consumer = preference.asConsumer()
        disposables.add(
            Observable.create { emitter ->
                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                        Unit

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        emitter.onNext(Optional.fromNullable(s.toString()))
                    }

                    override fun afterTextChanged(s: Editable?) = Unit
                }

                editText.addTextChangedListener(textWatcher)

                emitter.setCancellable {
                    editText.removeTextChangedListener(textWatcher)
                }
            }.subscribe(consumer)
        )
    }
}
