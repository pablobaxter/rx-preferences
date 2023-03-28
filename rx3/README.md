# Rx3 Preferences

[![Maven Central][1]][2] 

### Download

```groovy
implementation 'com.frybits.rx.preferences:rx3:1.0.0'
```

### Usage

Create a `Rx3SharedPreferences` instance which wraps a `SharedPreferences`:

```kotlin
val preferences = PreferenceManager.getDefaultSharedPreferences(context)
val rx3SharedPreferences = Rx3haredPreferences.create(preferences)
```

*Hint: Keep a strong reference on your `Rx3haredPreferences` instance for as long as you want to observe them to prevent listeners from being GCed.*

Create individual `Rx3Preference` objects:

```kotlin
val username = rx3SharedPreferences.getString("username")
val showWhatsNew = rx3SharedPreferences.getBoolean("show-whats-new", true)
```

Observe changes to individual preferences:

```kotlin
username.asObservable().subscribe { username ->
  Log.d(TAG, "Username: $username")
}
```

Subscribe preferences to streams to store values:

```kotlin
someBooleanRx3Stream.subscribe(showWhatsNew.asConsumer())
```

[1]:https://img.shields.io/maven-central/v/com.frybits.rx-preferences/rx3?label=rx3
[2]:https://search.maven.org/artifact/com.frybits.rx-preferences/rx3/1.0.0/aar
