package com.frybits.rx.preferences.rx3.app;

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

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.frybits.rx.preferences.core.Preference;
import com.frybits.rx.preferences.core.RxSharedPreferences;
import com.frybits.rx.preferences.rx3.Rx3Preference;
import com.frybits.rx.preferences.rx3.app.databinding.SampleLayoutBinding;
import com.google.common.base.Optional;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;

public class SampleActivityJava extends AppCompatActivity {

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleLayoutBinding binding = SampleLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RxSharedPreferences rxPreferences = RxSharedPreferences.create(getSharedPreferences("rx2", Context.MODE_PRIVATE));

        Preference<Boolean> fooBool = rxPreferences.getBoolean("fooBool");
        Preference<Optional<String>> fooString = Rx3Preference.asOptional(rxPreferences.getString("fooString"));

        bindPreference(binding.checkBox, fooBool);
        bindPreference(binding.checkBox2, fooBool);
        bindPreference(binding.text1, fooString);
        bindPreference(binding.text2, fooString);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    private void bindPreference(CheckBox checkBox, Preference<Boolean> preference) {
        // Bind the preference to the checkbox.
        disposables.add(Rx3Preference.asObservable(preference).subscribe(checkBox::setChecked));

        Consumer<Boolean> consumer = Rx3Preference.asConsumer(preference);
        disposables.add(
                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> emitter.onNext(isChecked);
                    checkBox.setOnCheckedChangeListener(listener);
                    emitter.setCancellable(() -> checkBox.setOnCheckedChangeListener(null));
                }).subscribe(consumer)
        );
    }

    private void bindPreference(EditText editText, Preference<Optional<String>> preference) {
        disposables.add(
                Rx3Preference.asObservable(preference)
                        .filter(stringOptional -> !editText.isFocused())
                        .subscribe(stringOptional -> editText.setText(stringOptional.orNull()))
        );

        Consumer<Optional<String>> consumer = Rx3Preference.asConsumer(preference);
        disposables.add(
                Observable.create((ObservableOnSubscribe<Optional<String>>) emitter -> {
                    TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            emitter.onNext(Optional.fromNullable(s.toString()));
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    };
                    editText.addTextChangedListener(textWatcher);

                    emitter.setCancellable(() -> editText.removeTextChangedListener(textWatcher));
                }).subscribe(consumer)
        );
    }
}