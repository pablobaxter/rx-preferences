# Coroutines/Flow Preferences

[![Maven Central][1]][2] 

### Download

```groovy
implementation 'com.frybits.rx.preferences:coroutines:1.1.0'
```

### Usage

Create a `RxSharedPreferences` instance which wraps a `SharedPreferences`:

```kotlin
val preferences = PreferenceManager.getDefaultSharedPreferences(context)
val rxPreferences = RxSharedPreferences.create(preferences)
```

*Hint: Keep a strong reference on your `RxSharedPreferences` instance for as long as you want to observe them to prevent listeners from being GCed.*

Create individual `Preference` objects:

```kotlin
val username = rxPreferences.getString("username")
val showWhatsNew = rxPreferences.getBoolean("show-whats-new", true)
```

Observe changes to individual preferences:

```kotlin
username.asFlow().collect { name ->
  Log.d(TAG, "Username: $name")
}
```

Subscribe preferences to streams to store values:

```kotlin
showWhatsNewView.checkedChanges().skipInitialValue()
    .collect(showWhatsNew.asCollector())
```
*(Note: `checkedChanges()` is from [FlowBinding](https://github.com/ReactiveCircus/FlowBinding))*

[1]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/coroutines?label=coroutines
[2]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/coroutines/1.1.0
