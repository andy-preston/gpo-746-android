# We need to use jammy not alpine because the Android build tools need glibc
FROM gradle:8.3.0-jdk17-jammy

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

RUN apt-get update && \
    apt-get -y --no-install-recommends install libusb-1.0-0-dev

CMD ["bash"]
