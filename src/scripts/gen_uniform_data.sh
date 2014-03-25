#!/bin/bash
# relationSize in 1000 10000 #100000 1000000 10000000

JAVA_HOME='/scratch/yluo/jdk1.7.0_51/'
#JAVA_HOME='/usr/lib/jvm/java-1.7.0-openjdk-amd64'

for ((relationSize=10000; relationSize <=10000000; relationSize=relationSize * 10)) 
do
#for setCard in 20 200 #2000 20000
for ((setCard=20; setCard <= 2000; setCard=setCard * 10))
do
	let elementCard=setCard*10 #elementCard is 10 times bigger
	#echo $elementCard	
$JAVA_HOME/bin/java -jar ./scj_gen.jar -r=$relationSize -c=uniform -m=$setCard -e=uniform -d=$elementCard > ./u\_$relationSize\_$setCard.data

done
done 
