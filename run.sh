#!/bin/sh

if [ $1 = "HTTPS" ]
then 
	echo "Starting backend Fitzhi in HTTPS mode"
	echo "-------------------------------------"
	echo "" 
	cp ./back-fitzhi/src/main/resources/application-https.properties ./deploy/backend-fitzhi/application-https.properties
elif [ $1 = "HTTP" ]
then
	echo "Starting backend Fitzhi in HTTP mode"
	echo "------------------------------------"
	echo ""
	rm ./deploy/backend-fitzhi/application-https.properties
else
    echo "Invalid profile given. Expecting either HTTP, or HTTPS."
	exit 1
fi

./deploy/backend-fitzhi/fitzhi-1.6-SNAPSHOT.jar --spring.profiles.active=$1

