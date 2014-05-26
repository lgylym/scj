#!/bin/bash

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'

#gen other distributions, set vary
for ((relationSize=2**17; relationSize <=2**17; relationSize=relationSize * 2)) #2^17
do
for ((domCard=2**14; domCard <= 2**14; domCard = domCard * 2))
do
for setCard in 8 32 128 512 2048
do
	while [ $(jobs | wc -l) -ge 4 ] ; do sleep 1 ; done
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=poisson -m=$setCard -e=uniform -d=$domCard > ./pu\_$relationSize\_$setCard\_$domCard.data &
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=poisson -d=$domCard > ./up\_$relationSize\_$setCard\_$domCard.data &
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=zipf -m=$setCard -e=uniform -d=$domCard > ./zu\_$relationSize\_$setCard\_$domCard.data &
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=zipf -d=$domCard > ./uz\_$relationSize\_$setCard\_$domCard.data &
done
done
done