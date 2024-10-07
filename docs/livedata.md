# LiveData

The livedata library adds extension functions to convert the `Prefrence` into `LiveData` and `Observer` objects.

## Getting the Dependency
```groovy
implementation "com.frybits.rx.preferences:livedata:2.0.0"
```

## Usage

### Preference as LiveData
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()

    val usernamePref: Preference<String?> = rxSharedPreferences.getString("username")

    val usernameLiveData: LiveData<String?> = usernamePref.asLiveData()

    usernameLiveData.asLiveData().observeForever { name ->
        println(name) // Immediately emits 'null'
    }

    usernamePref.value = "bob"
    // Output from livedata => "bob"
    ```
    
=== ":material-language-java: Java"

    ```java
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

    Preference<String> usernamePref = rxSharedPreferences.getString("username");

    LiveData<String> usernameLiveData = LiveDataPreference.asLiveData(usernamePref);

    usernameLiveData.observeForever(s -> {
        System.out.println(s); // Immediately emits 'null'
    });

    usernamePref.setValue("bob");
    // Output from livedata => "bob"
    ```

### Preferene as an Observer
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()

    val featureEnabledPref: Preference<Boolean> = rxSharedPreferences.getBoolean("somePref")

    val featureEnabledObserver = featureEnabledPref.asObserver()

    val checkButtonLiveData = object : LiveData<Boolean>() {

        val onChangedListener = OnCheckedChangeListener { _, isChecked ->
            value = isChecked
        }

        override fun onActive() {
            button.setOnCheckedChangeListener(onChangedListener)
        }

        override fun onInactive() {
            button.setOnCheckedChangeListener(null)
        }
    }

    checkButtonLiveData.observeForever(featureEnabledObserver)
    ```
    
=== ":material-language-java: Java"

    ```java
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

    Preference<Boolean> featureEnabledPref = rxSharedPreferences.getBoolean("somePref");

    Observer<Boolean> featureEnabledObserver = LiveDataPreference.asObserver(featureEnabledPref);

    LiveData<Boolean> checkButtonLiveData = new LiveData<>() {

        private OnCheckedChangeListener onChangedListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                value = isChecked;
            }
        };

        @Override
        public void onActive() {
            button.setOnCheckedChangeListener(onChangedListener);
        }

        @Override
        public void onInactive() {
            button.setOnCheckedChangeListener(null);
        }
    };

    checkButtonLiveData.observeForever(featureEnabledObserver);
    ```
