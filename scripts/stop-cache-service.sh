#!/bin/bash

# CACHE_PID=`netstat -anlp |  grep 8081 | awk '{print $7}' | tr "/java" " "`

PORT=8081
CACHE_PID=`lsof -i:$PORT | awk '{print $2}' | sed -n '2p'`

if [[ $CACHE_PID == "" ]]; then
    echo "cache service was stopped"
else
    echo "cache service is runing"
    set -x
    kill -9 $CACHE_PID
    set +x
    echo "cache service was killed"
fi

