# MysteriumVPN: a decentralized VPN

Mobile VPN app for Mysterium Network.

<a href='https://play.google.com/store/apps/details?id=network.mysterium.vpn&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width='261' /></a>

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

- To build and publish internal release automatically:
1. Create PR with bumped fastlane/android_version_code (Google play store requires new version code for each release).
2. Ater merge to master create new tag in github repository.
See [example](https://github.com/mysteriumnetwork/mysterium-vpn-mobile/commit/6111eb183e6aa9c5b2d12ed7bdc55eb598166c5a) commit.

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

### Updating CI image
Repository contains Dockerfile which includes OpenJDK and Fastlane to build, test and publish Android from Docker.

```
TAG=1.0.0
docker build -t mysteriumnetwork/mobile-ci:$TAG .
docker push mysteriumnetwork/mobile-ci:$TAG
```


## Contributing

### Android

#### Bump mobile-node version

- Update "mysterium.network:mobile-node:0.5-rc" gradle dependency to a [published version of mobile-node](https://bintray.com/mysteriumnetwork/maven/network.mysterium%3Amobile-node)

#### Native android logs

To see native android logs, use Logcat:

```bash
adb logcat
```
