fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## Android
### android buildDebug
```
fastlane android buildDebug
```
Builds the debug code
### android buildRelease
```
fastlane android buildRelease
```
Builds the release code
### android test
```
fastlane android test
```
Runs all the tests
### android build
```
fastlane android build
```
Build release build locally
### android internal
```
fastlane android internal
```
Push a new internal build to Play Store
### android bumpAndroidVersionCode
```
fastlane android bumpAndroidVersionCode
```
Increase Android code version
### android pushAndroidVersionCode
```
fastlane android pushAndroidVersionCode
```
Commit and push bumped version

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
