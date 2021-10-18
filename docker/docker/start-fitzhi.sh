if [ ${urlSonarServer} ] && [ ${urlSonarServer} != "" ]
then
	echo Sonar URL ${urlSonarServer}
	sed -i 's#proxy_pass http://host.docker.internal:9000/sonar;#proxy_pass '$urlSonarServer';#g' /etc/nginx/sites-enabled/default
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
	sed -i 's/"password": "#password"/"password": "'${password}'"/g' ../data/referential/sonar-servers.json
fi

service nginx start 

java -Xmx1g -jar fitzhi.jar --spring.profiles.active=HTTP
