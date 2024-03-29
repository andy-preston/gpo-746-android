FROM alpine:3.19.1 as builder

ADD https://github.com/denoland/deno/releases/download/v1.39.4/deno-x86_64-unknown-linux-gnu.zip \
    /usr/local/share/deno.zip

RUN unzip -d /usr/local/bin /usr/local/share/deno.zip


FROM python:3.12.1-bullseye

ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV PIP_ROOT_USER_ACTION=ignore
ENV MYPY_CACHE_DIR=/var/tmp/mypy
ENV PYLINTHOME=/var/tmp/pylint
ENV PYTHONPYCACHEPREFIX=/var/tmp/pycache
ENV DENO_DIR=/var/tmp/deno

COPY --from=builder /usr/local/bin/deno /usr/local/bin/deno

RUN pip install --upgrade pip && \
    pip install --upgrade \
        git+https://github.com/psf/black \
        pylint \
        mypy \
        schemdraw && \
    rm -rf /usr/share/doc/*
