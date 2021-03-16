sonar-scanner -X \
	-Dsonar.projectKey="Backend_Fitzhi" \
	-Dsonar.sources=./src/main/java \
	-Dsonar.java.binaries=./target/classes \
	-Dsonar.host.url=http://localhost:9000/sonar \
	-Dsonar.login=admin \
	-Dsonar.password=%1 


#  -Dsonar.login=57584d08e4ed7cd0c25a1e4e669f39df90a86098
