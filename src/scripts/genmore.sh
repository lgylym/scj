#!/bin/bash

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'

#third part, |R| vary, diff setcard as well
for ((relationSize=2**15; relationSize <=2**19; relationSize=relationSize * 2))
do
for ((domCard=2**14; domCard <= 2**14; domCard = domCard * 2))
do
for setCard in 128 512
do
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=uniform -d=$domCard > ./uu\_$relationSize\_$setCard\_$domCard.data
done
done
done