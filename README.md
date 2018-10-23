## Running

### Android

* [Install and run Tequilapi](https://github.com/MysteriumNetwork/node) on your PC
* [Install ADB](https://www.xda-developers.com/install-adb-windows-macos-linux)
* Connect Android phone, check if `adb` can see it:
```bash
adb devices
```
* Install React Native CLI:
```bash
npm install -g react-native-cli
```
* Install Android SDK, export `ANDROID_HOME` to SDK location, i.e.:
```bash
export ANDROID_HOME=/Users/<username>/Library/Android/sdk/
```

* Install dependencies:
```bash
yarn install
```

* Start application:
```bash
yarn start
```
## Contributing

* Running tests:
```bash
yarn test
```

* Running linter:
```bash
yarn lint
