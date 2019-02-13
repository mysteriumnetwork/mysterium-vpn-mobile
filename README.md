# MysteriumVPN mobile app

Application for VPN using Mysterium Network.

<a href='https://play.google.com/store/apps/details?id=network.mysterium.vpn&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## Running manually

* [Install and run Tequilapi](https://github.com/MysteriumNetwork/node) on your PC
* Install dependencies

```bash
yarn install
```

* Install React Native CLI:

```bash
yarn global add react-native-cli
npm install -g react-native-cli
```

### Android
* Setup crash reporting:
    * Download firebase crashlytics config - `google-services.json` from https://console.firebase.google.com
    * Place it in `android/app/`

#### Setup to run on actual device
* `brew cask install android-platform-tools` or [Install ADB](https://www.xda-developers.com/install-adb-windows-macos-linux)
* Connect Android phone, check if `adb` can see it:

```bash
adb devices
```

#### Setup Emulator
1. Download Android Studio
2. Android Studio comes with JDK preinstalled, but in case that version doesn't work, you need [JDK 1.8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
3. Setup Emulator in Android Studio and start it

#### Run Android:
Install Android SDK, export `ANDROID_HOME` to SDK location, i.e.:

```bash
export ANDROID_HOME=/Users/<username>/Library/Android/sdk/
```

Download mobile node dependency:

```bash
./util_scripts/download-node
```

Run app on default device (connected or emulator)
```bash
yarn android
```

#### Android Releases

- Get `google-services.json`:
    - Go to https://console.firebase.google.com
    - Open android project
    - Download `google-services.json`
    - Put it to `android/app/google-services.json`


- Create signing key:
    ```bash
    keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
    ```
    More info: https://facebook.github.io/react-native/docs/signed-apk-android#generating-a-signing-key

- Setup values in environment:
```bash
export FASTLANE_ANDROID_SIGNING_FILE_PATH=... # full path
export FASTLANE_ANDROID_SIGNING_KEYSTORE_PASS=...
export FASTLANE_ANDROID_SIGNING_KEY_ALIAS=...
export FASTLANE_ANDROID_SIGNING_KEY_PASS=...
```

- Setup Fastlane, more info in *fastlane/README.md*

##### Building release APK

- Make release build:
    ```bash
    fastlane android build
    ```
    
APK will be available under `android/app/build/outputs/apk/release/app-release.apk`

You can install this APK by:
- uploading it to phone, or
- using `adb install android/app/build/outputs/apk/release/app-release.apk`

##### Internal release

- Download android secret json from Play Console

- Specify downloaded file location
    ```bash
    export FASTLANE_ANDROID_SECRET_JSON_PATH=... # full path
    ```

- Build and publish internal release:
```bash
fastlane android beta
```

### iOS

* Setup crash reporting:
    * Download firebase crashlytics config - `GoogleService-Info.plist` from https://console.firebase.google.com
    * Place it in `ios/`

* Install X Code using App Store

* Set location for command line tools is X Code:

    * Open X Code
    * Go to "X Code" -> "Preferences" -> "Locations"
    * In "Command Line Tools", select single available option

* Install Cocoa dependency manager
 
```bash
brew bundle
```

```bash
cd ios
```

* Install Cocoa dependencies

```bash
carthage bootstrap --plaftorm iOS --cache-builds
```

```bash
cd ..
```

* Run iOS

```bash
yarn ios
```

* E2E tests

Make sure these are installed:
```bash
brew tap wix/brew
brew install wix/brew/applesimutils
```

Run E2E tests:

```bash
yarn test:e2e:ios
```

# Troubleshooting

### iOS build & run error

```bash
Command failed: /usr/libexec/PlistBuddy -c Print:CFBundleIdentifier build/Build/Products/Debug-iphonesimulator/MysteriumVPN.app/Info.plist
Print: Entry, ":CFBundleIdentifier", Does Not Exist
```

`Entry, ":CFBundleIdentifier", Does Not Exist` error is very generic, but doing the following steps fixed the issue.
 
#### Xcode configuration
* Open project
* File -> Project settings -> Advanced
* Select "custom"
* Set the following:
   
    Products: build/Build/Products
    
    Intermediates: build/Build/Intermediates.noindex
    
    Index Datastore: Index/DataStore 
* Product -> Build

### Android build error
```bash
* What went wrong:
A problem occurred configuring project ':react-native-fabric'.
> Could not resolve all artifacts for configuration ':react-native-fabric:classpath'.
   > Could not find lint-gradle-api.jar (com.android.tools.lint:lint-gradle-api:26.1.2).
```
related to [issue](https://github.com/corymsmith/react-native-fabric/issues/200#issuecomment-442051777)
waiting on [fix](https://github.com/corymsmith/react-native-fabric/pull/208)

to fix run:
```bash
./util_scripts/fix-react-native-fabric-gradle
```

If you come across an error `command not found: gsed`, you can fix it on OSX by running:
```bash
brew install gnu-sed
```

## Contributing

### Android

#### Bump mobile-node version

- To Update `implementation "mysterium.network:mobile-node:0.5-rc"` line in `./android/app/build.gradle`, published versions can be found [here](https://bintray.com/mysterium/Node/mobile-node).
- Node usage can be found in `MainActivity.java`.

#### Native android logs

To see native android logs, use Logcat:

```bash
adb logcat
```
