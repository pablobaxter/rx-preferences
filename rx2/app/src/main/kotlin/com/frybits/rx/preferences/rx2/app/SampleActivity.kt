package com.frybits.rx.preferences.rx2.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.frybits.rx.preferences.rx2.Rx2Preference
import com.frybits.rx.preferences.rx2.Rx2SharedPreferences
import com.frybits.rx.preferences.rx2.app.databinding.SampleLayoutBinding
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

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

class SampleActivity : AppCompatActivity() {

    private lateinit var binding: SampleLayoutBinding

    private lateinit var fooBool: Rx2Preference<Boolean>
    private lateinit var fooString: Rx2Preference<String>

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SampleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rx2Preferences =
            Rx2SharedPreferences.create(getSharedPreferences("rx2", MODE_PRIVATE))

        fooBool = rx2Preferences.getBoolean("fooBool")
        fooString = rx2Preferences.getString("fooString")

        bindPreference(binding.checkBox, fooBool)
        bindPreference(binding.checkBox2, fooBool)
        bindPreference(binding.text1, fooString)
        bindPreference(binding.text2, fooString)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    private fun bindPreference(checkBox: CheckBox, preference: Rx2Preference<Boolean>) {
        // Bind the preference to the checkbox.
        disposables.add(
            preference.asObservable()
                .subscribe { checkBox.isChecked = it }
        )

        // Bind the checkbox to the preference.
        val consumer = preference.asConsumer()
        disposables.add(
            Observable.create<Boolean> { emitter ->
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

    private fun bindPreference(editText: EditText, preference: Rx2Preference<String>) {
        disposables.add(
            preference.asObservable()
                .filter { !editText.isFocused }
                .subscribe { editText.setText(it) }
        )

        val consumer = preference.asConsumer()
        disposables.add(
            Observable.create<String> { emitter ->
                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                        Unit

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        emitter.onNext(s.toString())
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
