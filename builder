#!/bin/bash

GRADLE='/opt/gradle/gradle-7.0.2/bin/gradle'
SDK_MANAGER='/opt/android/cmdline-tools/bin/sdkmanager'
SDK_ROOT='/usr/local/share/android'
ADB='./share/platform-tools/adb'

case $1 in
'android')
    COMMAND='bash'
    CONT_NAME='android'
    ;;
'sdk')
    COMMAND="${SDK_MANAGER} --sdk_root=${SDK_ROOT} \
        platforms;android-31 build-tools;31.0.0"
    CONT_NAME='android'
    ;;
'build')
    COMMAND="${GRADLE} build"
    CONT_NAME='android'
    ;;
'test')
    COMMAND="${GRADLE} test"
    CONT_NAME='android'
    ;;
'assembly')
    COMMAND="make"
    CONT_NAME="gavrasm"
    ;;
'upload')
    $ADB install android/app/build/outputs/apk/debug/app-debug.apk
    exit
    ;;
'clean')
    rm -rf $(cat .gitignore) gradlew* .gitattributes
    exit
    ;;
*)
    echo "./builder android|sdk|build|test|clean"
    exit
    ;;
esac

DOCKER_DIR="$(dirname $0)/docker/${CONT_NAME}"
RUN_NAME="run-${CONT_NAME}"

docker build --tag ${CONT_NAME} ${DOCKER_DIR}

docker run \
    --rm --interactive --tty \
    --volume $(pwd):/usr/local/src \
    --volume $(pwd)/share:${SDK_ROOT} \
    --user $(id -u):$(id -g) \
    --name ${RUN_NAME} \
    ${CONT_NAME} ${COMMAND}
