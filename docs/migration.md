# Migrating from f2prateek/rx-preferences

Many of the APIs are similar to [f2prateek/rx-preferences][1], however there were some changes to packages.
This guide can be used for migrating from `f2prateek/rx-preferences` to `pablobaxter/rx-preferences` library.

## In project `build.gradle`
```diff
// build.gradle
- implementation 'com.f2prateek.rx.preferences2:rx-preferences'
+ implementation 'com.frybits.rx.preferences:core'
+ implementation 'com.frybits.rx.preferences:rx2'
```

## Import package changes
=== ":material-language-kotlin: Kotlin"
    ```diff
    // Rename for Preference class
    - import com.f2prateek.rx.preferences2.Preference
    + com.frybits.rx.preferences.core.Preference

    // Rename for RxSharedPreferences
    - import com.f2prateek.rx.preferences2.RxSharedPreferences
    + import com.frybits.rx.preferences.core.RxSharedPreferences

    // Additional import for RxJava 2
    + import com.frybits.rx.preferences.rx2.asObservable
    ```

=== ":material-language-java: Java"
    ```diff
    // Rename for Preference class
    - import com.f2prateek.rx.preferences2.Preference;
    + com.frybits.rx.preferences.core.Preference;

    // Rename for RxSharedPreferences
    - import com.f2prateek.rx.preferences2.RxSharedPreferences;
    + import com.frybits.rx.preferences.core.RxSharedPreferences;

    // Additional import for RxJava 2
    + import com.frybits.rx.preferences.rx2.Rx2Preference;

    // In code changes
    - preference.asObservable()
    + Rx2Preference.asObservable(preference)
    ```

[1]:https://github.com/f2prateek/rx-preferences