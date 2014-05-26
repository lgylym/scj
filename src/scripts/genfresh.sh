#!/bin/bash

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'

#first part, dom vary
for ((relationSize=2**17; relationSize <=2**17; relationSize=relationSize * 2)) #2^17
do
for ((domCard=2**10; domCard <= 2**14; domCard = domCard * 2))
do
for ((setCard=2**(4+1); setCard <= 2**(4+1); setCard=setCard * 2))
do
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=uniform -d=$domCard > ./uu\_$relationSize\_$setCard\_$domCard.data
done
done
done

#second part, set vary
for ((relationSize=2**17; relationSize <=2**17; relationSize=relationSize * 2)) #2^17
do
for ((domCard=2**14; domCard <= 2**14; domCard = domCard * 2))
do
for setCard in 8 128 512 2048 #skip 32
do
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=uniform -d=$domCard > ./uu\_$relationSize\_$setCard\_$domCard.data
done
done
done

#third part, |R| vary
for ((relationSize=2**15; relationSize <=2**19; relationSize=relationSize * 2))
do
for ((domCard=2**14; domCard <= 2**14; domCard = domCard * 2))
do
for ((setCard=2**(4+1); setCard <= 2**(4+1); setCard=setCard * 2))
do
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=uniform -d=$domCard > ./uu\_$relationSize\_$setCard\_$domCard.data
done
done
done