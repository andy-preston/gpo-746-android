FROM ubuntu:focal

ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV GRADLE_USER_HOME=/var/gradle/gradle
ENV ANDROID_SDK_ROOT=/usr/local/share/android
ENV KONAN_DATA_DIR=/var/gradle/konan
ENV REPO_OS_OVERRIDE=linux
ENV HOME=/var/tmp

WORKDIR /usr/local/src

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get -y --no-install-recommends install unzip openjdk-17-jdk-headless

ADD https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip \
    /usr/local/share/tools.zip

ADD https://downloads.gradle.org/distributions/gradle-8.3-bin.zip \
    /usr/local/share/gradle.zip

RUN mkdir /opt/android && unzip -d /opt/android /usr/local/share/tools.zip

RUN mkdir /opt/gradle && unzip -d /opt/gradle /usr/local/share/gradle.zip
