#!/bin/sh

export VERSION_FITZHI=`cat ../back-fitzhi/VERSION_FITZHI`
export SONAR_HOST_URL=`cat ./SONAR_HOST_URL`
export SONAR_TOKEN_LOGIN=`cat ./SONAR_TOKEN_LOGIN`

echo "Pull and run the Fitzhi container for the release ${VERSION_FITZHI} of Fitzhi."
echo "Sonar server is located @ ${SONAR_HOST_URL}."

# Terminating the last test if any.
docker stop fitzhi

# Test inside the Fitzhi infrastructure.
docker run --name fitzhi  \
-e "organization=fitzhi" \
-e "urlSonarServer=${SONAR_HOST_URL}" \
-e "login=${SONAR_TOKEN_LOGIN}" \
-v fitzhi-data:/fitzhi/deploy/ \
-p 80:80 -d --rm fitzhi/application:${VERSION_FITZHI}
