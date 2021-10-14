# Ubuntu Fitzhi container
FROM adoptopenjdk/openjdk11:armv7l-ubuntu-jre-11.0.12_7
LABEL Frederic VIDAL (frederic.vidal@fitzhi.com)


RUN apt update; \
    apt-get -y install git; \
    apt-get -y install nginx

RUN mkdir fitzhi; \
    cd fitzhi; \
    curl --insecure https://spoq.fitzhi.com/release/deploy.zip --output deploy.zip; \
    tar xvf deploy.zip; \
    cp deploy/docker/default /etc/nginx/sites-enabled/default; \
    cd /; \
    rm -rf spoq; \
    git clone https://github.com/fitzhi/spoq.git; \
    cd spoq; \
    rm -rf .git; \
    cd ..


RUN echo "service nginx start; java -Xmx1g -jar fitzhi.jar --spring.profiles.active=HTTP" >> fitzhi/deploy/backend-fitzhi/start-fitzhi.sh; \
    chmod +x fitzhi/deploy/backend-fitzhi/start-fitzhi.sh
WORKDIR /fitzhi/deploy/backend-fitzhi
# CMD service nginx start && java -Xmx1g -jar fitzhi.jar --spring.profiles.active=HTTP > ../out.txt
EXPOSE 80
