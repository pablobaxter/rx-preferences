# Rx Preferences

[![CircleCI][8]][9] [![GitHub][10]][11] [![Maven Central][12]][13]

For further documentation, visit the [project website][14]

A collection of libraries to allow `SharedPreferences` to be reactive

This library was created to continue improving on the great work that [@f2prateek][1] had done with [rx-preferences][2].

In order to handle the varying number of reactive frameworks available, there are multiple libraries available, which all share the same basic APIs and adapters.

## Available Reactive SharedPreferences Libraries

[Core][3]
```groovy
implementation 'com.frybits.rx.preferences:core:2.0.1'
```

[Coroutine/Flow][4]
```groovy
implementation 'com.frybits.rx.preferences:coroutine:2.0.1'
```

[LiveData][5]
```groovy
implementation 'com.frybits.rx.preferences:livedata:2.0.1'
```

[Rx2][6]
```groovy
implementation 'com.frybits.rx.preferences:rx2:2.0.1'
```

[Rx3][7]
```groovy
implementation 'com.frybits.rx.preferences:rx3:2.0.1'
```

For ease of ensuring all libraries are compatible, as they may have varying release cadences, a BOM is also provided:

```groovy
// Import BOM
implementation platform('com.frybits.rx.preferences:bom:2.0.1')
implementation 'com.frybits.rx.preferences:core'
implementation 'com.frybits.rx.preferences:livedata'
implementation 'com.frybits.rx.preferences:rx2'
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

[8]:https://dl.circleci.com/status-badge/img/gh/pablobaxter/rx-preferences/tree/master.svg?style=svg
[9]:https://dl.circleci.com/status-badge/redirect/gh/pablobaxter/rx-preferences/tree/master

[10]:https://img.shields.io/github/license/pablobaxter/rx-preferences
[11]:./LICENSE

[12]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/bom?label=bom
[13]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/bom/2.0.1
[14]:https://rx-preferences.frybits.com
