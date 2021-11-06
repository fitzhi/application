docker rmi fitzhi/application:1.6
docker volume rm fitzhi-data

# We timestamp the container.
sed -i '.original' "s/#buildingTime/'$(date)'/g" ./Dockerfile

# builing the container for Fitzhi
docker build --rm -t  fitzhi/application:1.6 .

# We reinitialize the .Dockerfile without the timestamp.
rm Dockerfile
mv Dockerfile.original Dockerfile

# Uploading the container for hub.docker.com
docker push fitzhi/application:1.6

# We remove the just built release of Fitzhi 
docker rmi fitzhi/application:1.6

# We download the last deployed release of fitzhi
docker pull fitzhi/application:1.6

# Test inside the Fitzhi infrastructure.
docker volume create fitzhi-data
docker run --name fitzhi \
-e "urlSonarServer=http://192.168.1.155:9000" \
-e "login=c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
-v fitzhi-data:/fitzhi/deploy/ \
-p 80:80 -d --rm fitzhi/application:1.6

# For debugging purpose only...
docker run --name fitzhi \
-e "urlSonarServer=http://192.168.1.155:9000" \
-e "login=c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
-v fitzhi-data:/fitzhi/deploy/ \
-p 80:80 -ti --rm fitzhi/application:1.6 /bin/bash


