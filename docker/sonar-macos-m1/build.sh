docker stop sonarqube
docker rm sonarqube
docker build -t sonarqube-m1:8.7.1-community .
