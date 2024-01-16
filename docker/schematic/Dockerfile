FROM python:3.12.1-bullseye

ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV PIP_ROOT_USER_ACTION=ignore
ENV PYLINTHOME=/var/tmp
ENV PYTHONPYCACHEPREFIX=/var/tmp
ENV DENO_DIR=/var/tmp

ADD run /usr/local/bin/run

ADD https://github.com/denoland/deno/releases/download/v1.39.4/deno-x86_64-unknown-linux-gnu.zip \
    /var/tmp/deno.zip

RUN unzip -d /usr/local/bin /var/tmp/deno.zip

RUN pip install --upgrade pip && \
    pip install --upgrade \
        git+https://github.com/psf/black \
        pylint \
        mypy \
        schemdraw && \
    rm -rf /usr/share/doc/*
