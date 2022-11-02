FROM ubuntu:focal
ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV DENO_DIR=/var/tmp
WORKDIR /usr/local/src/attiny

RUN apt-get update && \
    apt-get -y --no-install-recommends install \
        make ca-certificates unzip wget

RUN wget --progress=bar:force --output-document=/var/tmp/gavrasm.zip \
        http://www.avr-asm-tutorial.net/gavrasm/v54/gavrasm_lin_i386_x64_en_54.zip && \
    unzip -d /usr/local/bin /var/tmp/gavrasm.zip gavrasm_lin_i386_x64_en_v54 && \
    mv /usr/local/bin/gavrasm_lin_i386_x64_en_v54 /usr/local/bin/gavrasm

RUN wget --progress=bar:force --output-document=/var/tmp/deno.zip \
        https://github.com/denoland/deno/releases/download/v1.27.0/deno-x86_64-unknown-linux-gnu.zip && \
    unzip -d /usr/local/bin /var/tmp/deno.zip

RUN chmod a+x /usr/local/bin/* && \
    apt-get -y --purge remove \
        ca-certificates unzip wget && \
    apt-get -y --purge autoremove && \
    apt-get -y upgrade && \
    apt-get clean

