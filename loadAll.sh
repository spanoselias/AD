#!/bin/bash

mysql  < drop.sql

echo "All the tables are dropped"

mysql < create.sql
echo "Database is created"

javac src/MySAX_Mod.java

java MySAX_Mod ../ebay_data/items-*.xml

mysql EBY < load.sql
