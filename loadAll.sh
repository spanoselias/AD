#!/bin/bash

# Run the drop.sql batch file to drop existing tables.
# Inside the drop.sql, it is checked whether the table exists. They are Dropped them ONLY if they exist.
mysql  < drop.sql
echo "All the tables are dropped"

mysql < create_new.sql
echo "Database is created"

javac MySAX_Mod.java
echo "Java code is compiled"

java MySAX_Mod ebay-data/items-*.xml
echo "Java code is executed"

mysql EBY < load.sql
echo "Load csv files"

rm *.class
rm *.csv
