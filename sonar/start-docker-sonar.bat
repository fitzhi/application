docker stop fitzhi-sonarqube
docker rm fitzhi-sonarqube
docker run -d -v /opt/sonarqube/conf:/data --name fitzhi-sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 fitzhi-test:latest