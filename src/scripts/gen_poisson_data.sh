#!/bin/bash
# relationSize in 1000 10000 #100000 1000000 10000000

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'

for ((relationSize=10000; relationSize <=100000; relationSize=relationSize * 10)) 
do
#for setCard in 20 200 #2000 20000
for ((setCard=20; setCard <= 2000; setCard=setCard * 10))
do
	let elementCard=setCard*10
	#echo $elementCard	
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=poisson -m=$setCard -e=poisson -d=$elementCard > ./p\_$relationSize\_$setCard.data

done
done 
