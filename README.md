## Running

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

* `brew cask install android-platform-tools` or [Install ADB](https://www.xda-developers.com/install-adb-windows-macos-linux)
* Connect Android phone, check if `adb` can see it:
```bash
adb devices
```

* Install Android SDK, export `ANDROID_HOME` to SDK location, i.e.:

```bash
export ANDROID_HOME=/Users/<username>/Library/Android/sdk/
```

* Run Android:

```bash
yarn android
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

