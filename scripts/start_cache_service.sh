#!/usr/bin/env bash

# cd ../cache && mvn clean && mvn package

nohup java -jar cache-0.0.1-SNAPSHOT.jar >/root/eshop/logs/eshop-cache/eshop-cache.stdout.log 2>&1 &