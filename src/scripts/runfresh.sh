#!/bin/bash

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'


#1. test with signature length
for file in `ls -rS *.data`
do
	arrfile=(${file//\_/ })
	setCard=${arrfile[2]}
	low=$((setCard/16))
	if [ $low -eq 0 ] #low can be 0
		then low=1
	fi
	for ((siglen=$low; siglen<=$setCard; siglen*=2))
	do
	taskset -c 2 $JAVA_HOME/bin/java -Xmx5120m -jar ./scj_run.jar -r=$file -s=$file -j=ptsj -l=$siglen #>test_results
	done
done

#2. shj
for file in `ls -rS *.data`
do
	taskset -c 2 $JAVA_HOME/bin/java -Xmx5120m -jar ./scj_run.jar -r=$file -s=$file -j=shj
done

#3. pretti and pplus
for file in `ls -rS *.data`
do
	for join in 'pretti' 'pplus'
	do
		for ((iteration=0; iteration < 10; iteration++))
		do
		taskset -c 2 $JAVA_HOME/bin/java -Xmx5120m -jar ./scj_run.jar -r=$file -s=$file -j=$join
		done
	done
done
