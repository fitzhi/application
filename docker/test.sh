
#docker rmi fitzhi:1.6
docker stop fitzhi
docker rm fitzhi
docker build --rm=true -t fitzhi:1.6 .
#docker run --name fitzhi -p 80:80 -it --rm fitzhi:1.6 /bin/bash
docker run --name fitzhi --add-host=host.docker.internal:host-gateway -p 82:80 -it --rm fitzhi:1.6 /bin/bash
