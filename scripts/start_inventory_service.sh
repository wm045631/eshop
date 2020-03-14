#!/usr/bin/env bash

# cd ../inventory && mvn clean && mvn package

nohup java -jar inventory-0.0.1-SNAPSHOT.jar >/root/eshop/logs/eshop-inventory/eshop-inventory.stdout.log 2>&1 &
