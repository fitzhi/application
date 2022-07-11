#!/bin/sh

export VERSION_FITZHI=`cat ../../back-fitzhi/VERSION_FITZHI`
export SONAR_HOST_URL=`cat ../SONAR_HOST_URL`
export SONAR_TOKEN_LOGIN=`cat ../SONAR_TOKEN_LOGIN`
export FITZHI_URL="http://spoq.fitzhi.com:8082"
export FITZHI_LOGIN="myFakeUser"
export FITZHI_PASSWORD="myFakePassword"

echo "Pull and run the Fitzhi SLAVE container for the release ${VERSION_FITZHI} of Fitzhi."
echo "Sonar server is located @ ${SONAR_HOST_URL}."

# Terminating the last test if any.
docker stop slave

# Removing the just built release of Fitzhi 
docker rmi fitzhi/slave:${VERSION_FITZHI}
docker volume rm fitzhi-slave-data

# Downloading the last deployed release of fitzhi
docker pull fitzhi/slave:${VERSION_FITZHI}

# Test inside the Fitzhi infrastructure.
docker volume create fitzhi-slave-data
docker run --network host --name slave  \
   -e "organization=fitzhi" \
   -e "applicationUrl=${FITZHI_URL}" \
   -e "login=${FITZHI_LOGIN}" \
   -e "pass=${FITZHI_PASSWORD}" \
   -v fitzhi-slave-data:/fitzhi/deploy/ \
   -d --rm fitzhi/slave:${VERSION_FITZHI}

# sleep 1m
# curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/spring-projects/spring-boot"}' http://localhost:8081/api/project/analysis

# For debugging purpose only...
# docker run --name fitzhi  \
#  -e "organization=fitzhi" \
#  -e "urlSonarServer=${SONAR_HOST_URL}" \
#  -e "login=${SONAR_TOKEN_LOGIN}" \
#  -v fitzhi-data:/fitzhi/deploy/ \
#  -p 80:80 -ti --rm fitzhi/application:1.8-SNAPSHOT /bin/bash
