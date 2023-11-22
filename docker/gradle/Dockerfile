FROM ubuntu:focal

ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV REPO_OS_OVERRIDE=linux
ENV HOME=/var/tmp

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get -y --no-install-recommends install \
        unzip openjdk-17-jdk-headless libusb-1.0-0-dev

ADD https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip \
    /opt/tools.zip

RUN mkdir /opt/android && \
    unzip -d /opt/android /opt/tools.zip

ADD https://downloads.gradle.org/distributions/gradle-8.3-bin.zip \
    /opt/gradle.zip

RUN mkdir /opt/gradle && \
    unzip -d /opt/gradle /opt/gradle.zip

ADD http://www.avr-asm-tutorial.net/gavrasm/v54/gavrasm_lin_i386_x64_en_54.zip \
    /opt/gavrasm.zip

RUN mkdir /opt/gavrasm && \
    unzip -d /opt/gavrasm /opt/gavrasm.zip gavrasm_lin_i386_x64_en_v54 && \
    mv /opt/gavrasm/gavrasm_lin_i386_x64_en_v54 /opt/gavrasm/gavrasm