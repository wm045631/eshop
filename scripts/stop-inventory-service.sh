#!/bin/bash

# INVENTORY_PID=`netstat -anlp |  grep 8080 | awk '{print $7}' | tr "/java" " "`

PORT=8080
INVENTORY_PID=`lsof -i:$PORT | awk '{print $2}' | sed -n '2p'`

if [[ $INVENTORY_PID == "" ]]; then
    echo "inventory service was stopped"
else
    echo "inventory service is runing"
    set -x
    kill -9 $INVENTORY_PID
    set +x
    echo "inventory service was killed"
fi
