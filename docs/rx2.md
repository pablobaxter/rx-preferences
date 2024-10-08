# Rx2

The livedata library adds extension functions to convert the `Prefrence` into `Observable` and `Consumer` objects.

## Getting the Dependency
```groovy
implementation "com.frybits.rx.preferences:rx2:2.0.1"
```

## Usage

### Using non-nullable Preference with Rx2
Due to RxJava not allowing for `null` to be emitted, the `asOptional()` operator was created for `Preference` to convert any `Preference<T>` object into a `Preference<Optional<T>>` object. Note: This is the `com.google.common.base.Optional` class, and not the Java8 `Optional` class.
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()

    val usernamePref: Preference<String?> = rxSharedPreferences.getString("username")
    val optionalUsernamePref: Preference<Optional<String?>> = usernamePref.asOptional()
    ```
    
=== ":material-language-java: Java"

    ```java
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

    Preference<String> usernamePref = rxSharedPreferences.getString("username");
    Preference<Optional<String>> optionalUsernamePref = PreferenceUtil.asOptional(usernamePref);
    ```

### Preference as an Observable
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()

    val usernamePref: Preference<String?> = rxSharedPreferences.getString("username", null)

    // RxJava doesn't allow for 'null' values to be emitted. Make it optional instead.
    val optionalUsernamePref: Preference<Optional<String?>> = usernamePref.asOptional()

    val usernameObservable: Observable<Optional<String?>> = optionalUsernamePref.asObservable()

    usernameObservable.subscribe { name ->
        println(name.orNull()) // Immediately emits an empty Optional
    }

    usernamePref.value = "bob"
    // Output from RxJava => "bob"

    // You could also set the value via the optional prefrence as such
    optionalUsernamePref.value = Optional.fromNullable("foobar")
    // Output from RxJava => "foobar"

    // Or set it to null
    usernamePref.value = null
    // Output from RxJava => Empty optional
    ```
    
=== ":material-language-java: Java"

    ```java
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

    Preference<String> usernamePref = rxSharedPreferences.getString("username", null);

    // RxJava doesn't allow for 'null' values to be emitted. Make it optional instead.
    Preference<Optional<String>> optionalUsernamePref = PreferenceUtil.asOptional(usernamePref);

    Observable<Optional<String>> usernameObservable = Rx2Preference.asObservable(optionalUsernamePref);

    usernameObservable.subscribe(name -> {
        System.out.println(name.orNull()); // Immediately emits an empty Optional
    });

    usernamePref.setValue("bob");
    // Output from RxJava => "bob"

    // You could also set the value via the optional prefrence as such
    optionalUsernamePref.setValue(Optional.fromNullable("foobar"));
    // Output from RxJava => "foobar"

    // Or set it to null
    usernamePref.setValue(null);
    // Output from RxJava => Empty optional
    ```

### Preference as a Consumer
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()

    val featureEnabledPref: Preference<Boolean> = rxSharedPreferences.getBoolean("somePref")

    val featureEnabledConsumer = featureEnabledPref.asConsumer()

    val checkButtonObservable = Observable.create { emitter ->

        val onChangedListener = OnCheckedChangeListener { _, isChecked ->
            emitter.onNext(isChecked)
        }

        emitter.setCancellable {
            button.setOnCheckedChangeListener(null)
        }

        button.setOnCheckedChangeListener(onChangedListener)
    }

    checkButtonObservable.subscribe(featureEnabledConsumer)
    ```
    
=== ":material-language-java: Java"

    ```java
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

    Preference<Boolean> featureEnabledPref = rxSharedPreferences.getBoolean("somePref");

    Consumer<Boolean> featureEnabledConsumer = Rx2Preference.asConsumer(featureEnabledPref);

    Observable<Boolean> checkButtonObservable = Observable.create(emitter -> {

        OnCheckedChangeListener onChangedListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emitter.onNext(isChecked);
            }
        };

        emitter.setCancellable(() -> {
            button.setOnCheckedChangeListener(null);
        });

        button.setOnCheckedChangeListener(onChangedListener);
    });

    checkButtonObservable.subscribe(featureEnabledConsumer);
    ```
