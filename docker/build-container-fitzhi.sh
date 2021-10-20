
#docker rmi fitzhi:1.6
docker stop fitzhi
docker rm fitzhi
# --no-cache 
docker build --rm -t  fitzhi:1.6 .
#docker run --name fitzhi -p 80:80 -it --rm fitzhi:1.6 /bin/bash
docker volume create fitzhi-volume
docker volume ls
#--mount source=fitzhi-volume,target=/fitzhi/deploy/
docker run --name fitzhi --add-host=host.docker.internal:host-gateway \
-e "urlSonarServer=http://192.168.1.155:9000/sonar" \
-e "login=c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
 -p 80:80 -it --rm fitzhi:1.6 /bin/bash
