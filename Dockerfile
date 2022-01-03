FROM navikt/java:15
COPY ./build/libs/toi-kandidat-es-proxy-1.0-SNAPSHOT-all.jar app.jar

EXPOSE 8300
