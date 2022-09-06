#!/bin/bash

if [ ${urlSonarServer} ] && [ ${urlSonarServer} != "" ]
then
	echo Sonar URL ${urlSonarServer}
	sed -i 's#proxy_pass http://host.docker.internal:9000;#proxy_pass '$urlSonarServer';#g' /etc/nginx/sites-enabled/default
	cat ../data/referential/sonar-servers.json
fi

if [ ${user} ] && [ ${user} != "" ]
then
	echo Given user ${user}
	sed -i 's/"user": "admin",/"user": "'${user}'",/g' ../data/referential/sonar-servers.json
fi

if [ ${password} ] && [ ${password} != "" ]
then
	echo Given password ${password}
	sed -i 's/"password": "#password",/"password": "'${password}'",/g' ../data/referential/sonar-servers.json
fi

if [ ${login} ] && [ ${login} != "" ]
then
	echo Given login ${login}
	sed -i 's/"login": "#login"/"login": "'${login}'"/g' ../data/referential/sonar-servers.json
fi

if [ ${organization} ] && [ ${organization} != "" ]
then
	echo Given organization ${organization}
	sed -i 's/"organization": "#organization"/"organization": "'${organization}'"/g' ../data/referential/sonar-servers.json
fi

# This process listen the acivity of Fitzhi across the log activities
# The environment variable idle_timeout_limit (in minutes) setup the limit of idle time for Fitzhi
# If the server is passive during a delay greater than this limit, this process will stop the server.
if [ ${idle_timeout_limit} ] && [ ${idle_timeout_limit} -gt 0 ]
	./idle-fitzhi-control.sh &
fi

service nginx start 

echo " * Starting server fitzhi"
java -Xmx1g -jar fitzhi-1.9-SNAPSHOT.jar --spring.profiles.active="application, HTTP" > out.txt
