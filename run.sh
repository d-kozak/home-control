#!/usr/bin/env bash

echo "Compiling the project"
gradle jar || exit 1

echo "Starting the server"
java -Djava.util.logging.config.file=./log.properties --enable-preview  -jar server/build/libs/server.jar  > "out" &
echo "Server started, waiting a bit before starting the gateway"
sleep 5
java -Djava.util.logging.config.file=./log.properties --enable-preview  -jar gateway/build/libs/gateway.jar || exit 1
