#!/bin/bash

#test nljsig in the folder, try all files end with *.data
#example: ./test_join.sh nljsig > nlj.tests

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'

for file in `dir -d *.data`
do
$JAVA_HOME/bin/java -jar ./scj_run.jar -r=$file -s=$file -j=$1
done