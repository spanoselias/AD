#!/bin/bash

# Run the drop.sql batch file to drop existing tables.
# Inside the drop.sql, it is checked whether the table exists. They are Dropped them ONLY if they exist.
mysql  < drop.sql
echo "All the tables are dropped"

#The tables of the database are created
mysql < create.sql
echo "Database is created"

#The parse is compiled
javac MySAX_Mod.java
echo "Java code is compiled"

#The parse is executed
java MySAX_Mod ebay-data/items-*.xml
echo "Java code is executed"

#The csv files are loaded
mysql EBY < load.sql
echo "Load csv files"

#The class file and csv files are deleted
rm *.class
rm *.csv

#All the sql queries are executed
mysql EBY < queries.sql
