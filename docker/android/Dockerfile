FROM ubuntu:focal
ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV GRADLE_USER_HOME=/usr/local/src
ENV ANDROID_SDK_ROOT=/usr/local/share/android
ENV REPO_OS_OVERRIDE=linux
ENV HOME=/var/tmp
WORKDIR /usr/local/src/android

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get -y --no-install-recommends install \
        ca-certificates unzip wget \
        openjdk-11-jdk-headless

RUN wget --progress=bar:force --output-document=/var/tmp/gradle.zip \
        https://services.gradle.org/distributions/gradle-7.0.2-bin.zip && \
    mkdir /opt/gradle && \
    unzip -d /opt/gradle /var/tmp/gradle.zip

RUN wget --progress=bar:force --output-document=/var/tmp/tools.zip \
        https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    mkdir /opt/android && \
    unzip -d /opt/android /var/tmp/tools.zip
