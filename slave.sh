#!/bin/sh

if [ $1 = "HTTPS" ]
then 
	echo "Starting slave of Fitzhi in HTTPS mode"
	echo "--------------------------------------"
	echo "" 
	cp ./back-fitzhi/src/main/resources/application-https.properties ./deploy/backend-fitzhi/application-https.properties
elif [ $1 = "HTTP" ]
then
	echo "Starting slave of Fitzhi in HTTP mode"
	echo "-------------------------------------"
	echo ""
	rm ./deploy/backend-fitzhi/application-https.properties
else
    echo "Invalid profile given. Expecting either HTTP, or HTTPS."
	exit 1
fi

cp ./back-fitzhi/src/main/resources/application-slave.properties ./deploy/backend-fitzhi/application-https.properties
./deploy/backend-fitzhi/fitzhi-1.9-SNAPSHOT.jar --spring.profiles.active="slave, $1"
