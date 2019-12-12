# Hancel Parallel Zero

Hancel is an open source security app designed for Android mobiles having in mind journalists and activists, but useful for a more general public.

### Hancel is focused on three dimensions:

- Alert on life-threatening situation or physical imminent attack
- Offer secure & private communication tools
- Promote digital self-protection

### Features:

- [X] Hardware panic button (via power button)
- [X] Software panic button
- [X] Rings (Circle of partners for alarms via SMS)
- [ ] Share current location and tracking

**NOTE**: Current version is only for testing, this is a Beta version, help us improve it!
___

## Installation

<a href="http://play.google.com/store/apps/details?id=org.parallelzero.hancel" target="_blank"><img src="https://github.com/kike-canaries/android-hpma115s0/blob/master/assets/googleplay/gplayicon.png" align="right" width="128" ></a>

You can install it from the [release section](https://github.com/HancelParallelZero/hancel_android/releases) downloading the last apk or from [GooglePlay](http://play.google.com/store/apps/details?id=org.parallelzero.hancel)

## Building (optional)

For build a debug version please install Android SDK and Android Tools and follow the next steps:

``` bash
git clone --recursive https://github.com/HancelParallelZero/hancel_android.git
cd hancel_android
./gradlew assembleDebug
```
Is possible that the submodules will be updated in the future, for this, please run before:

``` bash
git submodule update --init --recursive
```

## Troubleshooting

For signed versions and possible `api-key` issues please generate your own API keys for the next services and put it in `app/src/main/res/values/api_keys.xml` like this:

```xml
<resources>
    <string name="key_debug" translatable="false">AIzaSyDvdxR50ECGFT600000000000000000000</string>
    <string name="key_prod" translatable="false">AIzaSyA-s1RQ-pzo8FlNOPkrVn1qy0000000000</string>
    <string name="crashlytics" translatable="false">78deae26fe9c8f597807a086900000000000000</string>
</resources>
```




