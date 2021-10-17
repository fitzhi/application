service nginx start 
if [ $urlSonarServer ] && [ ${urlSonarServer} != "" ]
then
    echo ${urlSonarServer}
    sed -i 's#"urlSonarServer": "http://localhost:80/sonar"#"urlSonarServer": "'${urlSonarServer}'"#g' ../data/referential/sonar-servers.json
    cat ../data/referential/sonar-servers.json
fi
java -Xmx1g -jar fitzhi.jar --spring.profiles.active=HTTP