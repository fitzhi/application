# 
export DOCKER_DEFAULT_PLATFORM=linux/amd64  
docker run --rm  -e SONAR_HOST_URL="http://192.168.1.155:9000/sonar" \
    -e SONAR_LOGIN="c1574b8a8ca259d527d7c8d1d630e6322f2b0455" \
    -e sonar.projectKey=$2 \
    -v "$1:/usr/src" \
    sonarsource/sonar-scanner-cli \
        -X \
        -Dsonar.projectKey="$2" \
        -Dsonar.sonar.projectVersion=1.0 \
        -Dsonar.sonar.sourceEncoding="UTF-8" \
        -Dsonar.sonar.host.url="http://192.168.1.155:9000"

#        -Dsonar.login="c1574b8a8ca259d527d7c8d1d630e6322f2b0455"
