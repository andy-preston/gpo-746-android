#!/bin/bash

GRADLE='/opt/gradle/gradle-7.0.2/bin/gradle'
SDK_MANAGER='/opt/android/cmdline-tools/bin/sdkmanager'
SDK_ROOT='/usr/local/share/android'
ADB='./share/platform-tools/adb'
#PROGRAMMER='-c usbasp'
#PROGRAMMER='-c stk500v1 -P /dev/ttyUSB0 -b 19200'
PROGRAMMER='-c stk500v1 -P net:avrnude.lan:5000'
AVRDUDE="avrdude ${PROGRAMMER} -p t2313 -V"

case $1 in
'android')
    COMMAND='bash'
    CONT_NAME='android'
    ;;
'sdk')
    COMMAND="${SDK_MANAGER} --sdk_root=${SDK_ROOT} platforms;android-31 build-tools;31.0.0"
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
    (cd attiny ; make "${2}")
    exit
    ;;
'program')
    HEX=$(awk -v t=$2 '$1 ~ t { print "attiny/" $2}' attiny/Makefile)
    $AVRDUDE -U "flash:w:${HEX}:i" -U "flash:v:${HEX}:i"
    exit
    ;;
'fuses')
    $AVRDUDE -U "lfuse:w:0xFF:m" -U "hfuse:w:0xDF:m" -U "efuse:w:0xFF:m"
    exit
    ;;
'clean')
    rm -rf $(cat .gitignore) gradlew* .gitattributes
    exit
    ;;
*)
    echo -e "Android App\n"
    echo "./builder android            - shell in build container"
    echo "./builder sdk                - install SDK"
    echo "./builder buildapp           - compile android app"
    echo "./builder testapp            - compile and run tests for app"
    echo "./builder install            - install APK file to android device"
    echo -e "\nATTiny firmware\n"
    echo "./builder assemble {target}  - assemble given target"
    echo "./builder program {target}   - program hex code into ATTiny"
    echo "./builder fuses              - program fuses into new ATTiny"
    echo -e "\nGeneral\n"
    echo "./builder clean              - clear out temporary files"
    echo -e "\nAssembly targets\n"
    grep -E ^[0-9]: attiny/Makefile
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
