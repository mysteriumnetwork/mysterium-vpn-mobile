#!/bin/sh

# This script adds github as remote when running from gitlab CI and
# pushes bumped android version code to github master.

setup_git() {
  git config user.email "core-services@mysterium.network"
  git config user.name "Mysterium Team"
}

commit_version_code() {
  git checkout -b bump-version
  git add ./fastlane/android_version_code 
  git commit --message "Test Bump Android version code"
}

upload_files() {
  git remote add github https://${GITHUB_MOBILE_API_TOKEN}@github.com/mysteriumnetwork/mysterium-vpn-mobile.git > /dev/null 2>&1
  git push --set-upstream github HEAD:master 
}

setup_git
commit_version_code
upload_files
