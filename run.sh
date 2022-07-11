#!/bin/sh

if [ $1 = "HTTPS" ];
then 
	echo "Starting backend Fitzhi in HTTPS mode"
	echo "-------------------------------------"
	echo "" 
	cp ./back-fitzhi/src/main/resources/application-https.properties ./deploy/backend-fitzhi/application-https.properties
elif [ $1 = "HTTP" ];
then
	echo "Starting backend Fitzhi in HTTP mode"
	echo "------------------------------------"
	echo ""
	rm ./deploy/backend-fitzhi/application-https.properties
else
    echo "Invalid profile given. Expecting either HTTP, or HTTPS."
	exit 1
fi

rm ./deploy/backend-fitzhi/application-slave.properties

cd ./deploy/backend-fitzhi/
java -Xmx1g -jar fitzhi-1.9-SNAPSHOT.jar --spring.profiles.active="application, $1" & echo $! > ./pid.file &
