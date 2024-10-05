# Coroutines

The coroutines library adds extension functions to collect changes occurring on a `Preference`, as wel as perform `commit()` operations with suspend functions.

**Note:** This library is not accessible from Java sources.

## Getting the dependency
```groovy
implementation "com.frybits.rx.preferences:coroutines:2.0.0"
```

## Usage

### Preference as a flow
```kotlin
val scope = CoroutineScope(Dispatchers.Main.immediate)

val usernamePref: Preference<String?> = rxPreferences.getString("username")

val usernameFlow: Flow<String?> = usernamePref.asFlow()

usernameFlow.onEach { name ->
    println(name) // Immediately emits 'null'
}.launchIn(scope)

usernamePref.value = "bob"
// Output from flow => "bob"
```

### Preference as a collector
```kotlin
val scope = CoroutineScope(Dispatchers.Main.immediate)

val featureEnabledPref: Preference<Boolean> = rxPreferences.getBoolean("somePref")

val featureEnabledCollector = featureEnabledPref.asCollector(committing = true) // Commit every emit

val checkedButtonFlow = channelFlow { 
    val onChangedListener = OnCheckedChangeListener { _, isChecked ->
        trySendBlocking(isChecked)
    }
    button.setOnCheckedChangeListener(onChangedListener)
    awaitClose { button.setOnCheckedChangeListener(null) }
}

scope.launch {
    // Every emit from the flow will be stored to the preference
    checkedButtonFlow.collect(featureEnabledCollector)
}
```
