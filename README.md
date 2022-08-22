# MysteriumVPN: a decentralized VPN

Mobile VPN app for Mysterium Network.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/network.mysterium.vpn/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=network.mysterium.vpn)

## Getting started (development)

1. Install Android Studio
2. `brew install --cask android-studio`
3. Download project's firebase crashlytics config - `google-services.json` from https://console.firebase.google.com/u/1/project/mysterium-vpn/overview and place it in `android/app`

### Local development

- Build Mysterium Node from source code:
    ```bash
    util_scripts/build-node.sh
    ```

- Uncomment local dependency in `android/app/build.gradle`:
    ```bash
    //implementation 'network.mysterium:mobile-node:0.8.1'
    implementation files('libs/Mysterium.aar')
    ```


### Building release APK

- Install Fastlane (if don't have it yet)
    ```bash
    brew install fastlane
    ```
    
- Make release build:
    ```bash
    source fastlane/.env.local && fastlane android build
    ```
    
APK will be available under `android/app/build/outputs/apk/release/app-release.apk`

You can install this APK by:
- uploading it to phone, or
- using `adb install android/app/build/outputs/apk/release/app-release.apk`

### Creating releases locally

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

## Releases

### Internal release

1. Create a PR with bumped fastlane/android_version_code (Google play store requires new version code for each release).
2. Ater merge to master create new tag in github repository.
See [example](https://github.com/mysteriumnetwork/mysterium-vpn-mobile/commit/6111eb183e6aa9c5b2d12ed7bdc55eb598166c5a) commit.

### Public release

Public releases are promoted and managed from the Google Play Console.

### Updating CI image

Repository contains Dockerfile which includes OpenJDK and Fastlane to build, test and publish Android from Docker.

```
TAG=1.0.0
docker build -t mysteriumnetwork/mobile-ci:$TAG .
docker push mysteriumnetwork/mobile-ci:$TAG
```

## Contributing

#### Bump mobile-node version

- Update "mysterium.network:mobile-node" gradle dependency to a [published version of mobile-node](https://maven.mysterium.network/releases/network/mysterium/mobile-node)
