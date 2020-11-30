
docker run --rm -e SONAR_HOST_URL="http://sqli70253:9000" --network=host -v "D:\work\projects\application\back-fitzhi/src/:/usr/src" sonarsource/sonar-scanner-cli -D sonar.login="admin" -D sonar.password="admin" -D sonar.projectKey="backend_Fitzhi" -Dsonar.java.binaries="..\target\classes" -X
