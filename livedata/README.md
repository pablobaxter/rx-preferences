# LiveData Preferences

[![Maven Central][1]][2] 

### Download

```groovy
implementation 'com.frybits.rx.preferences:livedata:1.1.0'
```

### Usage

Create a `RxSharedPreferences` instance which wraps a `SharedPreferences`:

Kotlin
```kotlin
val preferences = PreferenceManager.getDefaultSharedPreferences(context)
val rxPreferences = RxSharedPreferences.create(preferences)
```

Java
```java
SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
RxSharedPreferences rxSharedPreferences =  RxSharedPreferences.create(preferences);
```

*Hint: Keep a strong reference on your `RxSharedPreferences` instance for as long as you want to observe them to prevent listeners from being GCed.*

Create individual `Preference` objects:

Kotlin
```kotlin
val username = rxPreferences.getString("username")
val showWhatsNew = rxPreferences.getBoolean("show-whats-new", true)
```

Java
```java
Preference<String> username = rxPreferences.getString("username");
Preference<Boolean> showWhatsNew = rxPreferences.getBoolean("show-whats-new", true);
```

Observe changes to individual preferences:

Kotlin
```kotlin
username.asLiveData().observeForever { name ->
  Log.d(TAG, "Username: $name")
}
```

Java
```java
LiveDataPreference.asLiveData(username).observeForever(name -> {
    Log.d(TAG, "Username: " + name);
});
```

Subscribe preferences to streams to store values:

Kotlin
```kotlin
someBooleanLiveData.observeForever(showWhatsNew.asObserver())
```

Java
```java
someBooleanLiveData.observeForever(LiveDataPreference.asObserver(showWhatsNew));
```

[1]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/livedata?label=livedata
[2]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/livedata/1.1.0
