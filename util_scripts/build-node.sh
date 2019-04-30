#!/usr/bin/env bash

pushd "`dirname $0`/.."
    PATH_FULL=`pwd -P`
popd
LIBRARY_PATH="${PATH_FULL}/android/app/libs/Mysterium.aar"

pushd $GOPATH/src/github.com/mysteriumnetwork/node
    bin/package_android amd64
    cp -p build/package/Mysterium.aar $LIBRARY_PATH
popd