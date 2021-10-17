service nginx start 
if [ $urlSonarServer ] && [ ${urlSonarServer} != "" ]
then
    echo ${urlSonarServer}
    sed -i 's#proxy_pass http://host.docker.internal:9000/sonar;#proxy_pass '${urlSonarServer}';#g' /etc/nginx/sites-enabled/default
    cat ../data/referential/sonar-servers.json
fi
java -Xmx1g -jar fitzhi.jar --spring.profiles.active=HTTP
