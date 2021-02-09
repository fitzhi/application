
docker run --rm -e SONAR_HOST_URL="http://%1:9000" --network=host -v "D:\work\projects\application\front-fitzhi/src/:/usr/src" sonarsource/sonar-scanner-cli -D sonar.login="admin" -D sonar.password="admin" -D sonar.projectKey="frontEnd_Fitzhi" -D sonar.projectName="FrontEnd Fitzhi"
