# Rx Preferences

[![CircleCI][9]][10]
[![GitHub][11]][12]
[![Maven Central][13]][14] 

A collection of libraries to allow `SharedPreferences` to be reactive

This library was created to continue improving on the great work that [@f2prateek][1] had done with [rx-preferences][2].

In order to handle the varying number of reactive frameworks available, there are multiple libraries available, which all share the same basic APIs and adapters.

## Available Reactive SharedPreferences Libraries

- [Core][3]
  - `implementation 'com.frybits.rx.preferences:core:1.0.0'`
- [Coroutine/Flow][4]
  - `implementation 'com.frybits.rx.preferences:coroutine:1.0.0'`
- [LiveData][5]
  - `implementation 'com.frybits.rx.preferences:livedata:1.0.0'`
- [Rx2][6]
  - `implementation 'com.frybits.rx.preferences:rx2:1.0.0'`
- [Rx3][7]
  - `implementation 'com.frybits.rx.preferences:rx3:1.0.0'`

For ease of ensuring all libraries are compatible, as they may have varying release cadences, a BOM is also provided:

```groovy
// Import BOM
implementation platform('com.frybits.rx.preferences:bom:1.0.0')
implementation 'com.frybits.rx.preferences:core'
implementation 'com.frybits.rx.preferences:livedata'
```

## Migration from `:rx-preferences` to `:rx2`

Many of the APIs are similar to [f2prateek/rx-preferences][8], however there were some changes to packages and class names, mainly due to the addition of the other reactive frameworks.
This guide can be used for migrating from `:rx-preferences` to `:rx2` library (and potentially even `:rx3`).

1. Change Gradle dependency import from `com.f2prateek.rx.preferences2:rx-preferences` to `com.frybits.rx.preferences:rx2`
2. Rename all `com.f2prateek.rx.preferences2.Preference` imports to `com.frybits.rx.preferences.rx2.Rx2Preference`
3. Rename all `Preferences` class usages to `Rx2Preference`
4. Rename all `com.f2prateek.rx.preferences2.RxSharedPreferences` imports to `com.frybits.rx.preferences.rx2.Rx2SharedPreferences`
5. Rename all `RxSharedPreferences` class usages to `Rx2SharedPreferences`

All other API usages remain the same.

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

[13]:https://img.shields.io/maven-central/v/com.frybits.rx-preferences/bom?label=bom
[14]:https://search.maven.org/artifact/com.frybits.rx-preferences/bom/1.0.0/pom
