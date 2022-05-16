#!/bin/sh

export VERSION_FITZHI=`cat ../back-fitzhi/VERSION_FITZHI`
export SONAR_HOST_URL=`cat ./SONAR_HOST_URL`
export SONAR_TOKEN_LOGIN=`cat ./SONAR_TOKEN_LOGIN`

echo "Pull and run the Fitzhi container for the release ${VERSION_FITZHI} of Fitzhi."
echo "Sonar server is located @ ${SONAR_HOST_URL}."

# Terminating the last test if any.
docker stop fitzhi

# removing the previous image and data.
docker rmi fitzhi/application:${VERSION_FITZHI}
docker volume rm fitzhi-data

# Removing the just built release of Fitzhi 
docker rmi fitzhi/application:${VERSION_FITZHI}

# Downloading the last deployed release of fitzhi
docker pull fitzhi/application:${VERSION_FITZHI}

# Test inside the Fitzhi infrastructure.
docker volume create fitzhi-data
docker run --name fitzhi  \
-e "organization=fitzhi" \
-e "urlSonarServer=${SONAR_HOST_URL}" \
-e "login=${SONAR_TOKEN_LOGIN}" \
-v fitzhi-data:/fitzhi/deploy/ \
-p 80:80 -d --rm fitzhi/application:${VERSION_FITZHI}

# For debugging purpose only...
# docker run --name fitzhi  \
#  -e "organization=fitzhi" \
#  -e "urlSonarServer=${SONAR_HOST_URL}" \
#  -e "login=${SONAR_TOKEN_LOGIN}" \
#  -v fitzhi-data:/fitzhi/deploy/ \
#  -p 80:80 -ti --rm fitzhi/application:1.8-SNAPSHOT /bin/bash
