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
'buildapp')
    COMMAND="${GRADLE} build"
    CONT_NAME='android'
    ;;
'testapp')
    COMMAND="${GRADLE} test"
    CONT_NAME='android'
    ;;
'install')
    $ADB install android/app/build/outputs/apk/debug/app-debug.apk
    exit
    ;;
'buildavr')
    cd attiny
    make
    cd ..
    exit
    ;;
'program')
    for JOB in w v
    do
        avrdude -c usbasp -p t2313 -V -U "flash:${JOB}:${2}:i"
    done
    exit
    ;;
'clean')
    rm -rf $(cat .gitignore) gradlew* .gitattributes
    exit
    ;;
*)
    echo "./builder android|sdk|buildapp|testapp|install|buildavr|program|clean"
    exit
    ;;
esac

docker build --tag android_gpo "$(dirname $0)/docker"

docker run \
    --rm --interactive --tty \
    --volume $(pwd):/usr/local/src \
    --volume $(pwd)/share:${SDK_ROOT} \
    --user $(id -u):$(id -g) \
    --name android_gpo_run \
    android_gpo ${COMMAND}