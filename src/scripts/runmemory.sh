#!/bin/bash

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'


#1. test with signature length
for file in `ls -rS uu_131072_*_16384.data`
do
	arrfile=(${file//\_/ })
	setCard=${arrfile[2]}
	siglen=$((setCard/4))
	taskset -c 4 $JAVA_HOME/bin/java -Xmx5120m -jar ./scj_run.jar -r=$file -s=$file -j=ptsj -l=$siglen #>test_results
done

#2. shj
for file in `ls -rS uu_131072_*_16384.data`
do
	taskset -c 4 $JAVA_HOME/bin/java -Xmx5120m -jar ./scj_run.jar -r=$file -s=$file -j=shj
done

#3. pretti and pplus
for file in `ls -rS uu_131072_*_16384.data`
do
	for join in 'pretti' 'pplus'
	do
		taskset -c 4 $JAVA_HOME/bin/java -Xmx5120m -jar ./scj_run.jar -r=$file -s=$file -j=$join
	done
done
