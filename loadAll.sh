#!/bin/bash

mysql  < drop.sql

echo "All the tables are dropped"

mysql < create_new.sql
echo "Database is created"

javac src/MySAX_Mod.java
echo "Java code is compiled"

rm -r tmp
mkdir tmp

cp src/MySAX_Mod.class tmp/

cp -R ebay-data/items-*.xml tmp/
cp Csvs/load.sql tmp/

cd tmp/

java MySAX_Mod items-*.xml
echo "Java code is executed"

rm *.xml
rm *.class

mysql EBY < load.sql
