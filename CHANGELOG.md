# Change Log

### Version 2.0.1 / 2024-10-07
* Add license to PreferenceUtil.kt
* Fix crash "IllegalStateException: Recursive update" on Coroutines library (#23)
* Deprecates getObjectNonNull() function (#24)
* Correct the exception message when incorrect sharedprefereces is listened to (#26)
* Add in docs and website for Rx-Preferences (#25)
* Deprecates `RxSharedPreferences.getObjectNonNull()` in favor of `RxSharedPreferences.getObject()`

### Version 2.0.0 / 2024-09-30
* **Breaking Change release**
* Use extension functions for Rx libraries
  * This removes the uses of the various `*SharedPreference` classes, and provides a single `RxSharedPreferences` class
  * This also removes the various `*Preference` classes in favor of a single `Preference` class
  * All reactive frameworks are now accessible as extension functions against the `Preference` class.
    * Java will have util classes to provide the access to extension functions
* Update CI to build against latest android images
* Update several dependencies
* Remove deprecated APIs from rx-sharedpreferences
* Add in utility to convert `Preference<T>` to `Preference<Optional<T>>` for reactive frameworks that don't allow `null`.

### Version 1.1.0 / 2023-10-03
* Update dependencies
* Move to compile sdk 34
* Use vanniktech for maven publishing
* Update CI script so that publishing occurs via CI now
* Remove build health checks from CI (for now)

### Version 1.0.0 - ALL / 2023-03-27
* Fork of [f2prateek/rx-preferences](https://github.com/f2prateek/rx-preferences)
  * Due to group ID change and package change, this project will start the versioning at `1.0.0` for all libraries.
* `rx2`
  * Initial release
  * Contains fix for [f2prateek/rx-preferences#141](https://github.com/f2prateek/rx-preferences/issues/141)
* `rx3`
  * Initial release
  * Fix for [f2prateek/rx-preferences#135](https://github.com/f2prateek/rx-preferences/issues/135)
* `coroutines`, `livedata`, `core`, `bom`
  * Initial release

---
### **=== Migration to `com.frybits.rx.preferences`** ===
---

Version 2.0.1 *(18-09-2020)*
--------------------------------

Promotes `2.0.1-beta1` to a stable release.


Version 2.0.1-beta1 *(15-04-2020)*
--------------------------------

* [Fix](https://github.com/f2prateek/rx-preferences/pull/132): Improve handling for `null` values stored in preferences.

* [Improvement](https://github.com/f2prateek/rx-preferences/pull/124): Update dependencies and tools.


Version 2.0.0 *(22-04-2018)*
----------------------------

Promotes `2.0.0-RC3` to a stable release. If you are migrating from v1, please refer to the changelog for earlier RC versions to see what has changed.

Version 2.0.0-RC3 *(13-08-2017)*
--------------------------------

* [New](https://github.com/f2prateek/rx-preferences/pull/92): Add ability to clear preferences via `RxSharedPreferences`.

* [Fix](https://github.com/f2prateek/rx-preferences/pull/98): The default value for `RxSharedPreferences#getObject` may not be null, but was incorrectly annotated as `@Nullable`. This corrects the annotation to `@NonNull`.


Version 2.0.0-RC2 *(27-04-2017)*
--------------------------------

This release includes multiple breaking changes.

* [New](https://github.com/f2prateek/rx-preferences/pull/75): Replace `Adapter` with `Converter`. A `Converter` is a simpler interface that only deals with serialization, and abstracts away the complexity of working with `SharedPreferences` or `SharedPreferences.Editor`.

```java
/**
 * Converts instances of {@code T} to be stored and retrieved as Strings in {@link
 * SharedPreferences}.
 */
interface Converter<T> {
  /**
   * Deserialize to an instance of {@code T}. The input is retrieved from {@link
   * SharedPreferences#getString(String, String)}.
   */
  @NonNull T deserialize(@NonNull String serialized);

  /**
   * Serialize the {@code value} to a String. The result will be used with {@link
   * SharedPreferences.Editor#putString(String, String)}.
   */
  @NonNull String serialize(@NonNull T value);
}
```

 * [New](https://github.com/f2prateek/rx-preferences/commit/0424808557c308108b0af7fcd046a7d047fde486): Disallow null values from being emitted by the Preference observable. Specifically `RxSharedPreferences#getEnum` and `RxSharedPreferences#getObject` do not allow null values to be used as a default value.

 * [New](https://github.com/f2prateek/rx-preferences/pull/85): Make `Preference#set` only accept non null values. Trying to call `set` with a null value will now throw an exception.

 * [New](https://github.com/f2prateek/rx-preferences/pull/65): Make the `Set<String>` returned by `RxSharedPreferences#getStringSet` unmodifiable.

 * [Improvement](https://github.com/f2prateek/rx-preferences/pull/68): Use `@RequiresApi` instead of `@TargetApi`.


Version 2.0.0-RC1 *(25-12-2016)*
--------------------------------

rx-preferences has been updated to support RxJava 2.0. The `Preference` type is now an interface, but the core itself is mostly unchanged. `2.0.0-RC1` does not handle backpressure yet.

Because the release includes breaking API changes, we're changing the project's package name from `com.f2prateek.rx.preferences` to `com.f2prateek.rx.preferences2`. The maven group has also changed to `com.f2prateek.rx.preferences2`. This should make it possible for large applications and libraries to migrate incrementally.

Version 1.0.2 *(15-06-2016)*
----------------------------

 * Remove custom backpressure support in favor of RxJava 1.1's built-in [buffer latest](https://github.com/f2prateek/rx-preferences/pull/39).


Version 1.0.1 *(28-10-2015)*
----------------------------

 * Add support for [backpressure](https://github.com/f2prateek/rx-preferences/pull/27).
 * Use [reasonable defaults](https://github.com/f2prateek/rx-preferences/pull/29) for creating Preferences that store primitives.


Version 1.0.0 *(23-08-2015)*
----------------------------

Initial release.
