# MysteriumVPN mobile app

Application for VPN using Mysterium Network.

<a href='https://play.google.com/store/apps/details?id=network.mysterium.vpn&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## Running manually

### Android
* Setup crash reporting:
    * Ensure You have permission to the project here https://console.firebase.google.com
    * Download project's firebase crashlytics config - `google-services.json` from https://console.firebase.google.com/u/1/project/mysterium-vpn/overview
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

Run app on default device (connected or emulator)
```bash
Open Android Studio and click Run button :)
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
cp fastlane/.env.local.dist fastlane/.env.local
vim fastlane/.env.local
```

- Setup Fastlane, more info in *fastlane/README.md*

##### Local development

- Build Mysterium Node from source code:
    ```bash
    util_scripts/build-node.sh
    ```

- Uncomment local dependency in `android/app/build.gradle`:
    ```bash
    //implementation 'network.mysterium:mobile-node:0.8.1'
    implementation files('libs/Mysterium.aar')
    ```


##### Building release APK

- Install Fastlane (if don't have it yet)
    ```bash
    brew cask install fastlane
    ```
    
- Make release build:
    ```bash
    source fastlane/.env.local && fastlane android build
    ```
    
APK will be available under `android/app/build/outputs/apk/release/app-release.apk`

You can install this APK by:
- uploading it to phone, or
- using `adb install android/app/build/outputs/apk/release/app-release.apk`

##### Internal release

- To build and publish internal release manaully:
Increase bundle_release versionCode + 1 in fastlane/Fastlane file and run:
```bash
source fastlane/.env.local && fastlane android internal
```

- To build and publish internal release manaully automatically create new tag in github repository.

### iOS

iOS App is under development.

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
- Node usage can be found in `MainActivity.kt`.

#### Native android logs

To see native android logs, use Logcat:

```bash
adb logcat
```
