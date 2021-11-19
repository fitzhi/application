# Terminating the last test if any.
docker stop fitzhi

# removing the previous image and data.
docker rmi fitzhi/application:1.6-SNAPSHOT
docker volume rm fitzhi-data

# Timestamping the container.
sed -i '.original' "s/#buildingTime/'$(date)'/g" ./Dockerfile

# Builing the container for Fitzhi
docker build --rm -t  fitzhi/application:1.6-SNAPSHOT .

# We reinitialize the Dockerfile without the timestamp.
rm Dockerfile
mv Dockerfile.original Dockerfile

# Uploading the container for hub.docker.com
docker push fitzhi/application:1.6-SNAPSHOT

# Removing the just built release of Fitzhi 
docker rmi fitzhi/application:1.6-SNAPSHOT

# Downloading the last deployed release of fitzhi
docker pull fitzhi/application:1.6-SNAPSHOT

# Test inside the Fitzhi infrastructure.
docker volume create fitzhi-data
docker run --name fitzhi \
-e "urlSonarServer=http://192.168.1.155:9000" \
-e "login=c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
-v fitzhi-data:/fitzhi/deploy/ \
-p 80:80 -d --rm fitzhi/application:1.6-SNAPSHOT

# For debugging purpose only...
#docker run --name fitzhi \
#-e "urlSonarServer=http://192.168.1.155:9000" \
#-e "login=c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
#-v fitzhi-data:/fitzhi/deploy/ \
#-p 80:80 -ti --rm fitzhi/application:1.6 /bin/bash


