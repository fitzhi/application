#!/bin/sh

export VERSION_FITZHI=`cat ../../back-fitzhi/VERSION_FITZHI`
export SONAR_HOST_URL=`cat ../SONAR_HOST_URL`
export SONAR_TOKEN_LOGIN=`cat ../SONAR_TOKEN_LOGIN`


# For debugging purpose only...
docker stop slave
docker run --network host --name slave  \
  -e "organization=fitzhi" \
  -e "urlApplication=http://localhost:8080" \
  -e "login=myFakeUser" \
  -e "pass=myFakePassword" \
  -v fitzhi-data:/fitzhi/deploy/ \
  -ti --rm fitzhi/slave:1.9-SNAPSHOT /bin/bash
