# Rx3 Preferences

[![Maven Central][1]][2] 

### Download

```groovy
implementation 'com.frybits.rx.preferences:rx3:1.1.0'
```

### Usage

Create a `RxSharedPreferences` instance which wraps a `SharedPreferences`:

Kotlin
```kotlin
val preferences = PreferenceManager.getDefaultSharedPreferences(context)
val rxSharedPreferences = RxSharedPreferences.create(preferences)
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
val username = rxSharedPreferences.getString("username")
val showWhatsNew = rxSharedPreferences.getBoolean("show-whats-new", true)
```

Java
```java
Preference<String> username = rxPreferences.getString("username");
Preference<Boolean> showWhatsNew = rxPreferences.getBoolean("show-whats-new", true);
```

Observe changes to individual preferences:

Kotlin
```kotlin
username.asObservable().subscribe { name ->
  Log.d(TAG, "Username: $name")
}
```

Java
```java
Rx3Preference.asObservable(preference).subscribe(name -> {
  Log.d(TAG, "Username: " + name);
});
```

Subscribe preferences to streams to store values:

Kotlin
```kotlin
RxCompoundButton.checks(showWhatsNewView)
    .subscribe(showWhatsNew.asConsumer())
```

Java
```java
RxCompoundButton.checks(showWhatsNewView)
    .subscribe(Rx3Preference.asConsumer(preference))
```
*(Note: `RxCompoundButton` is from [RxBinding](https://github.com/JakeWharton/RxBinding))*

[1]:https://img.shields.io/maven-central/v/com.frybits.rx.preferences/rx3?label=rx3
[2]:https://central.sonatype.com/artifact/com.frybits.rx.preferences/rx3/1.1.0
