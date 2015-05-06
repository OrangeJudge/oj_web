#!/usr/bin/env bash

if [ -f RUNNING_PID ]; then
    echo "Shutting down running server."
    kill -9 `cat RUNNING_PID`
    rm RUNNING_PID
fi
