#docker rmi fitzhi:1.6
docker build --rm -t fitzhi:1.6 .
#docker run --name fitzhi -p 80:80 -it --rm fitzhi:1.6 /bin/bash
docker run --name fitzhi -p 81:80 -it --rm fitzhi:1.6 /bin/bash
