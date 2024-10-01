# Rx Preferences

[![CircleCI][9]][10]
[![GitHub][11]][12]
[![Maven Central][13]][14] 

A collection of libraries to allow `SharedPreferences` to be reactive

This library was created to continue improving on the great work that [@f2prateek][1] had done with [rx-preferences][2].

In order to handle the varying number of reactive frameworks available, there are multiple libraries available, which all share the same basic APIs and adapters.

## Available Reactive SharedPreferences Libraries

- [Core][3]
  - `implementation 'com.frybits.rx.preferences:core:2.0.0'`
- [Coroutine/Flow][4]
  - `implementation 'com.frybits.rx.preferences:coroutine:2.0.0'`
- [LiveData][5]
  - `implementation 'com.frybits.rx.preferences:livedata:2.0.0'`
- [Rx2][6]
  - `implementation 'com.frybits.rx.preferences:rx2:2.0.0'`
- [Rx3][7]
  - `implementation 'com.frybits.rx.preferences:rx3:2.0.0'`

For ease of ensuring all libraries are compatible, as they may have varying release cadences, a BOM is also provided:

```groovy
// Import BOM
implementation platform('com.frybits.rx.preferences:bom:2.0.0')
implementation 'com.frybits.rx.preferences:core'
implementation 'com.frybits.rx.preferences:livedata'
implementation 'com.frybits.rx.preferences:rx2'
```

## Migration from `f2prateek/rx-preferences` to `pablobaxter/rx-preferences`

Many of the APIs are similar to [f2prateek/rx-preferences][8], however there were some changes to packages.
This guide can be used for migrating from `f2prateek/rx-preferences` to `pablobaxter/rx-preferences` library:

1. Change Gradle dependency import from `com.f2prateek.rx.preferences2:rx-preferences` to `com.frybits.rx.preferences:core`
2. Rename all `com.f2prateek.rx.preferences2.Preference` imports to `com.frybits.rx.preferences.core.Preference`
3. Rename all `com.f2prateek.rx.preferences2.RxSharedPreferences` imports to `com.frybits.rx.preferences.core.RxSharedPreferences`
4. Add an import of the following package...
   5. In Kotlin: `import com.frybits.rx.preferences.rx2.asObservable`
   6. In Java: `import com.frybits.rx.preferences.rx2.Rx2Preference;` and change instances of `preference.asObservable()` to `Rx2Preference.asObservable(preference)`

**NOTE**: Version 1.x of `pablobaxter/rx-preferences` had multiple "Preference" classes for each reactive framework. This has since been changed, so that all libraries share the same class.

## Usage
The `core` library provides the entryway to getting the `RxSharedPreferences`. This allows creating the `Preference<T>` objects that represent the requested preference, as well as providing a base for the various extension functions used to create the reactive object.

### Quick start (Kotlin)

Core:
```kotlin
val rxSharedPreferences: RxSharedPreferences = getSharedPreferences("foobar", MODE_PRIVATE).asRxSharedPreferences()
val username: Preference<String?> = rxSharedPreferences.getString("username")

println(username.value) // Outputs value of "username" preference
username.value = "myName" // Sets the preference to "myName"
```

Coroutines:
```kotlin
username.asFlow().collect { name ->
    println("Username: $name") // Outputs value of "username" preference
}

flow {
  emit("coroutineName")
}.collect(username.asCollector()) // username is updated to "coroutineName"
```

LiveData:
```kotlin
username.asLiveData().observeForever { name ->
  println("Username: $name") // Outputs value of "username" preference
}

MutableLiveData("livedataName")
  .observeForever(username.asObserver()) // username is updated to "livedataName"
```

Rx2 and Rx3:
```kotlin
username.asOptional() // Helper operator to convert nullable types to com.google.common.base.Optional<>
  .asObservable()
  .subscribe { name ->
    println("Username: ${name.get()}") // Outputs value of "username" preference
  }

Observable.just(Optional.fromNullable("observableName"))
    .subscribe(
      username.asOptional() // Helper operator to convert nullable types to com.google.common.base.Optional<>
          .asConsumer() // username is updated to "observableName"
    )
```

### Quickstart (Java)

Core:
```java
RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(getSharedPreferences("foobar", MODE_PRIVATE));
Preference<String> username = rxSharedPreferences.getString("username");

String name = username.getValue();

System.out.println(name); // Outputs value of "username" preference
username.setValue("myName"); // Sets the preference to "myName"
```

**Note:** Coroutines library is not accessible to Java

LiveData:
```java
LiveDataPreference.asLiveData(username).observeForever (s -> {
    System.out.println("Username: " + s); // Outputs value of "username" preference
});

LiveData<String> stringLiveData = new MutableLiveData("livedataName");
stringLiveData.observeForever(LivedataPreference.asObserver(username)); // username is updated to "livedataName"
```

Rx2:
```java
// Helper operator to convert nullable types to com.google.common.base.Optional<>
Preference<Optional<String>> optionalUsername = PreferenceUtil.asOptional(username);

Rx2SharedPreference.asObservable(optionalUsername).subscribe(s -> {
    System.out.println("Username: " + s.orNull()); // Outputs value of "username" preference
});

Observable<Optional<String>> rx2Observable = Observable.just(Optional.fromNullable("observableName"));
rx2Observable.subscribe(Rx2SharedPreferences.asConsumer(optionalUsername)); // username is updated to "observableName"
```

Rx3:
```java
// Helper operator to convert nullable types to com.google.common.base.Optional<>
Preference<Optional<String>> optionalUsername = PreferenceUtil.asOptional(username);

Rx3SharedPreference.asObservable(optionalUsername).subscribe(s -> {
    System.out.println("Username: " + s.orNull()); // Outputs value of "username" preference
});

Observable<Optional<String>> rx3Observable = Observable.just(Optional.fromNullable("observableName"));
rx3Observable.subscribe(Rx3SharedPreferences.asConsumer(optionalUsername)); // username is updated to "observableName"
```

License
-------
```
    Copyright 2014 Prateek Srivastava

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```

[1]:https://github.com/f2prateek
[2]:https://github.com/f2prateek/rx-preferences
[3]:./core
[4]:./coroutines
[5]:./livedata
[6]:./rx2
[7]:./rx3
[8]:https://github.com/f2prateek/rx-preferences

[9]:https://dl.circleci.com/status-badge/img/gh/pablobaxter/rx-preferences/tree/master.svg?style=svg
[10]:https://dl.circleci.com/status-badge/redirect/gh/pablobaxter/rx-preferences/tree/master

[11]:https://img.shields.io/github/license/pablobaxter/rx-preferences
[12]:./LICENSE

[13]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/bom?label=bom
[14]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/bom/2.0.0
