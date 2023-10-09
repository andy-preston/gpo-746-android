FROM python:3.11.1-bullseye

ENV TERM=linux
ENV DEBIAN_FRONTEND=noninteractive
ENV PYLINTHOME=/var/tmp

ADD run /usr/local/bin/run

RUN pip install --upgrade pip && \
    pip install --upgrade \
        git+https://github.com/psf/black \
        pylint \
        schemdraw \
        xmlformatter && \
    rm -rf /usr/share/doc/*
