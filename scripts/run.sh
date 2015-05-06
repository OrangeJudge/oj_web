#!/usr/bin/env bash

# This script will start the Orange Judge web application in the backend.
# If there is a running one, it will be shutted down.

if [ -f RUNNING_PID ]; then
    echo "Shutting down running server."
    kill -9 `cat RUNNING_PID`
    rm RUNNING_PID
fi

echo "Starting server."

nohup ./bin/oj_web -mem 512 -J-server -Dconfig.file=./site.conf > /dev/null 2> /dev/null < /dev/null  &

echo "Starting started."
