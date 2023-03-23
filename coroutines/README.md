# Coroutines/Flow Preferences

### Download

```groovy
implementation 'com.frybits.rx.preferences:coroutines:3.0.0'
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
