package com.frybits.rx.preferences.livedata.app;

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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.frybits.rx.preferences.core.Preference;
import com.frybits.rx.preferences.core.RxSharedPreferences;
import com.frybits.rx.preferences.livedata.LiveDataPreference;
import com.frybits.rx.preferences.livedata.app.databinding.SampleLayoutBinding;

public class SampleActivityJava extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleLayoutBinding binding = SampleLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RxSharedPreferences livedataPreferences = RxSharedPreferences.create(getSharedPreferences("livedata", MODE_PRIVATE));

        Preference<Boolean> fooBool = livedataPreferences.getBoolean("fooBool");
        Preference<String> fooString = livedataPreferences.getString("fooString");

        bindPreference(binding.checkBox, fooBool);
        bindPreference(binding.checkBox2, fooBool);
        bindPreference(binding.text1, fooString);
        bindPreference(binding.text2, fooString);
    }

    private void bindPreference(CheckBox checkBox, Preference<Boolean> preference) {
        // Bind the preference to the checkbox.

        LiveDataPreference.asLiveData(preference).observe(this, checkBox::setChecked);

        // Bind the checkbox to the preference.
        Observer<Boolean> observer = LiveDataPreference.asObserver(preference);
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> mutableLiveData.setValue(isChecked);
        checkBox.setOnCheckedChangeListener(listener);
        mutableLiveData.observe(this, observer);
    }

    private void bindPreference(EditText editText, Preference<String> preference) {
        LiveDataPreference.asLiveData(preference).observe(this, s -> {
            if (!editText.isFocused()) {
                editText.setText(s);
            }
        });

        Observer<String> observer = LiveDataPreference.asObserver(preference);
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mutableLiveData.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editText.addTextChangedListener(textWatcher);
        mutableLiveData.observe(this, observer);
    }
}
