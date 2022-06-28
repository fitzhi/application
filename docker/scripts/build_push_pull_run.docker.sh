#!/bin/sh

export VERSION_FITZHI=`cat ../back-fitzhi/VERSION_FITZHI`
export SONAR_HOST_URL=`cat ./SONAR_HOST_URL`
export SONAR_TOKEN=`cat ./SONAR_TOKEN`

echo "Build, push and test the Fitzhi container for the release ${VERSION_FITZHI} of Fitzhi."
echo "Sonar server is located @ ${SONAR_HOST_URL}."

# Terminating the last test if any.
docker stop fitzhi

# removing the previous image and data.
docker rmi fitzhi/application:${VERSION_FITZHI}
docker volume rm fitzhi-data

# Timestamping the container.
sed -i '.original' "s/#buildingTime/'$(date)'/g" ./Dockerfile

# Builing the container for Fitzhi
docker build --rm -t  fitzhi/application:${VERSION_FITZHI} .

# We reinitialize the Dockerfile without the timestamp.
rm Dockerfile
mv Dockerfile.original Dockerfile

# Uploading the container for hub.docker.com
docker push fitzhi/application:${VERSION_FITZHI}

# Removing the just built release of Fitzhi 
docker rmi fitzhi/application:${VERSION_FITZHI}

# Downloading the last deployed release of fitzhi
docker pull fitzhi/application:${VERSION_FITZHI}

# Test inside the Fitzhi infrastructure.
docker volume create fitzhi-data
docker run --name fitzhi \
-e "urlSonarServer=http://${SONAR_HOST_URL}:9000" \
-e "login=${SONAR_TOKEN}" \
-v fitzhi-data:/fitzhi/deploy/ \
-p 80:80 -d --rm fitzhi/application:${VERSION_FITZHI}

# For debugging purpose only...
#docker run --name fitzhi \
#-e "urlSonarServer=http://${SONAR_HOST_URL}:9000" \
#-e "login=c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
#-v fitzhi-data:/fitzhi/deploy/ \
#-p 80:80 -ti --rm fitzhi/application:1.8-SNAPSHOT /bin/bash


