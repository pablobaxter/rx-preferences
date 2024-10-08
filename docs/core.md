# Core

The core library provides the `RxSharedPreferences` class that contains APIs to get typed `Preference` objects, which represent the preferences stored in the Android `SharedPreferences`.

## Getting the dependency
```groovy
implementation "com.frybits.rx.preferences:core:2.0.0"
```

## Usage

### Creating `RxSharedPreferences`
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val rxSharedPreferences = sharedPreferences.asRxSharedPreferences()
    ```
    
=== ":material-language-java: Java"

    ```java
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);
    ```

### Basic usage of preferences
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    // Using a string preference
    val usernamePref: Preference<String?> = rxSharedPreferences.getString("username") // Default value optional

    println(usernamePref.value) // null
    
    usernamePref.value = "bob"

    println(usernamePref.value) // "bob"
    ```
    
=== ":material-language-java: Java"

    ```java
    // Using a string prefrence
    Preference<String> usernamePref = rxSharedPreferences.getString("username"); // Default value optional

    System.out.println(usernamePref.getValue()); // null

    usernamePref.setValue("bob");

    System.out.println(usernamePref.getValue()); // "bob"
    ```

### Using Enums
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    enum class Cats {
        TABBY,
        BLACK,
        ORANGE
    }
    val catsPref: Preference<Cats> = rxSharedPreferences.getEnum("cats", Cats.TABBY)

    println(catsPref.value) // "TABBY"

    catsPref.value = Cats.ORANGE
    
    println(catsPref.value) // "ORANGE"
    ```
    
=== ":material-language-java: Java"

    ```java
    enum Cats {
        TABBY,
        BLACK,
        ORANGE
    }
    Preference<Cats> catsPref = rxSharedPreferences.getEnum("cats", Cats.TABBY, Cats.class);

    System.out.println(catsPref.getValue()); // "TABBY"

    catsPref.setValue(Cats.ORANGE);
    
    System.out.println(catsPref.getValue()); // "ORANGE"
    ```

### Using custom types
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    @Serializable
    data class Foobar(val someString: String = "", val someInt: Int = 0)

    // Create the converter
    val converter = object : Converter<Foobar?> {
        override fun deserialize(serialized: String?): Foobar? {
            return serialized?.let { Json.decodeFromString<Foobar>(it) }
        }

        override fun serialize(value: Foobar?): String? {
            return value?.let { Json.encodeToString(it) }
        }
    }

    val foobarPref: Preference<Foobar?> = rxSharedPreferences.getObject("foobar", null, converter)

    println(foobarPref.value) // null

    foobarPref.value = Foobar("str", 42)
    
    println(foobarPref.value) // "Foobar(someString=str, someNum=42)"
    ```
    
=== ":material-language-java: Java"

    ```java
    public static class Foobar {
        final String someString;
        final int someInt;

        public Foobar() {
            someString = "";
            someInt = 0;
        }

        public Foobar(String someString, int someInt) {
            this.someString = someString;
            this.someInt = someInt;
        }
        
        @NonNull
        @Override
        public String toString() {
            return "Foobar(someString=" + someString + ", someInt=" + someInt + ")";
        }
    }

    // Assuming Gson usage in Java
    Gson gson = new Gson();

    // Create the converter
    Preference.Converter<Foobar> converter = new Preference.Converter<Foobar>() {
        @Nullable
        @Override
        public Foobar deserialize(@Nullable String serialized) {
            if (serialized == null) {
                return new Foobar();
            }
            return gson.fromJson(serialized, Foobar.class);
        }

        @Nullable
        @Override
        public String serialize(@Nullable Foobar value) {
            if (value == null) {
                return null;
            }
            return gson.toJson(value);
        }
    };
    Preference<Foobar> foobarPref = rxSharedPreferences.getObject("foobar", null, converter);

    System.out.println(foobarPref.getValue()); // null

    foobarPref.setValue(new Foobar("str", 42));
    
    System.out.println(foobarPref.getValue()); // "Foobar(someString=str, someInt=42)"
    ```

### Clearing the preferences
=== ":material-language-kotlin: Kotlin"

    ```kotlin
    // Using a string preference
    val usernamePref: Preference<String?> = rxSharedPreferences.getString("username", "bob") // Default value optional

    println(usernamePref.isSet) // false
    println(usernamePref.defaultValue) // "bob"
    println(usernamePref.value) // "bob"

    usernamePref.value = "domokun"

    println(usernamePref.isSet) // true
    println(usernamePref.defaultValue) // "bob"
    println(usernamePref.value) // "domokun"

    // Clears this preference
    usernamePref.delete()
    // or to clear everything
    // rxSharedPreferences.clear() 

    println(usernamePref.isSet) // false
    println(usernamePref.defaultValue) // "bob"
    println(usernamePref.value) // "bob"
    ```
    
=== ":material-language-java: Java"

    ```java
    // Using a string prefrence
    Preference<String> usernamePref = rxSharedPreferences.getString("username", "bob"); // Default value optional

    System.out.println(usernamePref.isSet()); // false
    System.out.println(usernamePref.getDefaultValue()); // "bob"
    System.out.println(usernamePref.getValue()); // "bob"

    usernamePref.setValue("domokun");

    System.out.println(usernamePref.isSet()); // true
    System.out.println(usernamePref.getDefaultValue()); // "bob"
    System.out.println(usernamePref.getValue()); // "domokun"

    // Clears this preference
    usernamePref.delete();
    // or to clear everything
    // rxSharedPreferences.clear();

    System.out.println(usernamePref.isSet()); // false
    System.out.println(usernamePref.getDefaultValue()); // "bob"
    System.out.println(usernamePref.getValue()); // "bob"
    ```
