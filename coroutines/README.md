# Coroutines/Flow Preferences

[![Maven Central][1]][2] 

### Download

```groovy
implementation 'com.frybits.rx.preferences:coroutines:1.0.0'
```

### Usage

Create a `CoroutineSharedPreferences` instance which wraps a `SharedPreferences`:

```kotlin
val preferences = PreferenceManager.getDefaultSharedPreferences(context)
val coroutinePreferences = CoroutineSharedPreferences.create(preferences)
```

*Hint: Keep a strong reference on your `CoroutineSharedPreferences` instance for as long as you want to observe them to prevent listeners from being GCed.*

Create individual `CoroutinePreference` objects:

```kotlin
val username = coroutinePreferences.getString("username")
val showWhatsNew = coroutinePreferences.getBoolean("show-whats-new", true)
```

Observe changes to individual preferences:

```kotlin
username.asFlow().collect { username ->
  Log.d(TAG, "Username: $username")
}
```

Subscribe preferences to streams to store values:

```kotlin
showWhatsNewView.checkedChanges().skipInitialValue()
    .collect(showWhatsNew.asCollector())
```
*(Note: `checkedChanges()` is from [FlowBinding](https://github.com/ReactiveCircus/FlowBinding))*

[1]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/coroutines?label=coroutines
[2]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/coroutines/1.0.0
