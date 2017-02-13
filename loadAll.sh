#!/bin/bash

# Run the drop.sql batch file to drop existing tables.
# Inside the drop.sql, it is checked whether the table exists. They are Dropped them ONLY if they exist.
mysql  < drop.sql
echo "All the tables are dropped"


mysql < create_new.sql
echo "Database is created"

javac src/MySAX_Mod.java
echo "Java code is compiled"

rm -r tmp
echo "tmp folder is deleted if exist"

mkdir tmp
echo "tmp folder is created"

cp src/MySAX_Mod.class tmp/

cp -R ebay-data/items-*.xml tmp/
cp Csvs/load.sql tmp/

cd tmp/

java MySAX_Mod items-*.xml
echo "Java code is executed"

rm *.xml
rm *.class

mysql EBY < load.sql
