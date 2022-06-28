#!/bin/sh

export VERSION_FITZHI=`cat ../../back-fitzhi/VERSION_FITZHI`
export SONAR_HOST_URL=`cat ../SONAR_HOST_URL`
export SONAR_TOKEN_LOGIN=`cat ../SONAR_TOKEN_LOGIN`


# For debugging purpose only...
docker run --network=host --name slave  \
  -e "organization=fitzhi" \
  -e "urlSonarServer=${SONAR_HOST_URL}" \
  -e "login=${SONAR_TOKEN_LOGIN}" \
  -v fitzhi-data:/fitzhi/deploy/ \
  -p 80:80 -ti --rm fitzhi/slave:1.9-SNAPSHOT /bin/bash
