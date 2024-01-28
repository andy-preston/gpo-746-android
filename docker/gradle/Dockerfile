FROM alpine:3.19.1 as builder

ADD https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip \
    /usr/local/share/tools.zip

ADD http://www.avr-asm-tutorial.net/gavrasm/v54/gavrasm_lin_i386_x64_en_54.zip \
    /usr/local/share/gavrasm.zip

RUN apk add unzip && \
    mkdir /opt/android && \
    unzip -d /opt/android /usr/local/share/tools.zip && \
    mkdir /opt/gavrasm && \
    unzip -d /opt/gavrasm /usr/local/share/gavrasm.zip \
        gavrasm_lin_i386_x64_en_v54 && \
    mv /opt/gavrasm/gavrasm_lin_i386_x64_en_v54 /opt/gavrasm/gavrasm

FROM gradle:8.3.0-jdk17-jammy

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get -y --no-install-recommends install libusb-1.0-0-dev

CMD ["bash"]

COPY --from=builder /opt/android /opt/android
COPY --from=builder /opt/gavrasm /opt/gavrasm
