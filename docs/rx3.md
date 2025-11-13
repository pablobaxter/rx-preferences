# Rx3

The livedata library adds extension functions to convert the `Prefrence` into `Observable` and `Consumer` objects.

## Getting the Dependency
```groovy
implementation "com.frybits.rx.preferences:rx3:2.1.0"
```

## Usage

### Preference as an Observable
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()

    val usernamePref: Preference<String> = rxSharedPreferences.getString("username", "")

    val usernameObservable: Observable<String> = usernamePref.asObservable()

    usernameObservable.subscribe { name ->
        println(name) // Immediately emits ""
    }

    usernamePref.value = "bob"
    // Output from RxJava => "bob"
    ```
    
=== ":material-language-java: Java"

    ```java
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

    Preference<String> usernamePref = rxSharedPreferences.getString("username", "");

    Observable<String> usernameObservable = Rx3Preference.asObservable(usernamePref);

    usernameObservable.subscribe(name -> {
        System.out.println(name.orNull()); // Immediately emits ''
    });

    usernamePref.setValue("bob");
    // Output from RxJava => "bob"
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

    Consumer<Boolean> featureEnabledConsumer = Rx3Preference.asConsumer(featureEnabledPref);

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
