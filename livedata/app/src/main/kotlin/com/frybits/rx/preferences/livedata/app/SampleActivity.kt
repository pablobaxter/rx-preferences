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

package com.frybits.rx.preferences.livedata.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.frybits.rx.preferences.core.Preference
import com.frybits.rx.preferences.core.RxSharedPreferences.Companion.asRxSharedPreferences
import com.frybits.rx.preferences.livedata.app.databinding.SampleLayoutBinding
import com.frybits.rx.preferences.livedata.asLiveData
import com.frybits.rx.preferences.livedata.asObserver

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = SampleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val livedataPreferences =
            getSharedPreferences("livedata", MODE_PRIVATE).asRxSharedPreferences()

        val fooBool = livedataPreferences.getBoolean("fooBool")
        val fooString = livedataPreferences.getString("fooString")

        bindPreference(binding.checkBox, fooBool)
        bindPreference(binding.checkBox2, fooBool)
        bindPreference(binding.text1, fooString)
        bindPreference(binding.text2, fooString)
    }

    private fun bindPreference(checkBox: CheckBox, preference: Preference<Boolean>) {
        // Bind the preference to the checkbox.
        preference.asLiveData()
            .observe(this) { checkBox.isChecked = it }

        // Bind the checkbox to the preference.
        val observer = preference.asObserver()
        val mutableLiveData = MutableLiveData<Boolean>()
        val listener = OnCheckedChangeListener { _, isChecked ->
            mutableLiveData.value = isChecked
        }
        checkBox.setOnCheckedChangeListener(listener)
        mutableLiveData.observe(this, observer)
    }

    private fun bindPreference(editText: EditText, preference: Preference<String?>) {
        preference.asLiveData()
            .observe(this) {
                if (!editText.isFocused) {
                    editText.setText(it)
                }
            }

        val observer = preference.asObserver()
        val mutableLiveData = MutableLiveData<String?>()
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mutableLiveData.value = s?.toString()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }
        editText.addTextChangedListener(textWatcher)
        mutableLiveData.observe(this, observer)
    }
}
