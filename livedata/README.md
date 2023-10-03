# LiveData Preferences

[![Maven Central][1]][2] 

### Download

```groovy
implementation 'com.frybits.rx.preferences:livedata:1.1.0'
```

### Usage

Create a `LiveDataSharedPreferences` instance which wraps a `SharedPreferences`:

```kotlin
val preferences = PreferenceManager.getDefaultSharedPreferences(context)
val liveDataSharedPreferences = LiveDataSharedPreferences.create(preferences)
```

*Hint: Keep a strong reference on your `LiveDataSharedPreferences` instance for as long as you want to observe them to prevent listeners from being GCed.*

Create individual `LiveDataPreference` objects:

```kotlin
val username = liveDataSharedPreferences.getString("username")
val showWhatsNew = liveDataSharedPreferences.getBoolean("show-whats-new", true)
```

Observe changes to individual preferences:

```kotlin
username.asLiveData().observeForever { username ->
  Log.d(TAG, "Username: $username")
}
```

Subscribe preferences to streams to store values:

```kotlin
someBooleanLiveData.observeForever(showWhatsNew.asObserver())
```

[1]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/livedata?label=livedata
[2]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/livedata/1.1.0
