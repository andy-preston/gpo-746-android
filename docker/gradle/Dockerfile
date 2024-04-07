FROM gradle:8.3.0-jdk17-alpine

ADD https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip \
    /usr/local/share/tools.zip

RUN mkdir /opt/android && \
    unzip -d /opt/android /usr/local/share/tools.zip && \
    mv /opt/android/cmdline-tools/ /opt/android/latest && \
    mkdir /opt/android/cmdline-tools/ && \
    mv /opt/android/latest /opt/android/cmdline-tools/latest

RUN yes | /opt/android/cmdline-tools/latest/bin/sdkmanager \
        'platforms;android-31' \
        'build-tools;31.0.0'

ENV ANDROID_SDK_ROOT=/opt/android

# Temporary bodge to get GAVRASM from local file system
# www.avr-asm-tutorial.net seems to be no longer available
# We need a more permanent solution but I haven't worked out what that solution
# will be at this stage
ADD gavrasm_lin_i386_x64_en_54.zip /usr/local/share/gavrasm.zip
#ADD http://www.avr-asm-tutorial.net/gavrasm/v54/gavrasm_lin_i386_x64_en_54.zip \
#    /usr/local/share/gavrasm.zip

RUN mkdir /opt/gavrasm && \
    unzip -d /opt/gavrasm /usr/local/share/gavrasm.zip \
        gavrasm_lin_i386_x64_en_v54 && \
    mv /opt/gavrasm/gavrasm_lin_i386_x64_en_v54 /opt/gavrasm/gavrasm

    RUN apk add libusb-dev

CMD ["bash"]
