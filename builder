#!/bin/bash

GRADLE='/opt/gradle/gradle-7.0.2/bin/gradle'
SDK_MANAGER='/opt/android/cmdline-tools/bin/sdkmanager'
SDK_ROOT='/usr/local/share/android'
ADB='./share/platform-tools/adb'
AVRDUDE='avrdude -c usbasp -p t2313 -V'

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
'assemble')
    cd attiny
    make "${2}"
    cd ..
    exit
    ;;
'program')
    for JOB in w v
    do
        $AVRDUDE -U "flash:${JOB}:${2}:i"
    done
    exit
    ;;
'fuses')
    $AVRDUDE -U "lfuse:w:0x${2}:m" -U "hfuse:w:0x${3}:m" -U "efuse:w:0x${4}:m"
    exit
    ;;
'clean')
    rm -rf $(cat .gitignore) gradlew* .gitattributes
    exit
    ;;
*)
    echo "./builder android | sdk | buildapp | testapp | install | assemble target | program hex-file | clean"
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
