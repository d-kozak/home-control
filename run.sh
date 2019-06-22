#!/usr/bin/env bash

echo "Compiling the project"
gradle jar || exit 1

echo "Starting the server"
java -jar server/build/libs/server.jar  > "out" &
echo "Server started, waiting a bit before starting the gateway"
sleep 5
java -jar gateway/build/libs/gateway.jar || exit 1
