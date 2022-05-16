
sonar-scanner -X \
  -Dsonar.projectKey="Front_Fitzhi" \
  -Dsonar.projectName="Front Fitzhi" \
  -Dsonar.sources=. \
  -Dsonar.host.url=http://localhost:9000/sonar \
  -Dsonar.login=$1 \
  -Dsonar.password=$2 


#  -Dsonar.login=57584d08e4ed7cd0c25a1e4e669f39df90a86098
