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

* Install Cocoa dependency manager
 
```bash
brew install carthage
```

```bash
cd ios
```

* Install Cocoa dependencies

```bash
carthage bootstrap
```

```bash
cd ..
```

* Run iOS

```bash
yarn ios
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

