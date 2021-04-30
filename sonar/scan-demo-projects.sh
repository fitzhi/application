#!/bin/bash

cd ../deploy/data/repos/3
echo "Build project Spring Framework"
# ./gradlew build

directories = (	"spring-aop" "spring-aspects" "spring-beans" "spring-context" "spring-context-indexer" "spring-context-support" "spring-core" "spring-expression" "spring-instrument" "spring-jdbc" "spring-jms" "spring-messaging" "spring-orm" "spring-oxm" "spring-r2dbc" "spring-test" "spring-tx" "spring-webflux" "spring-webmvc" "spring-websocket")

for dir in "${directories[@]}"
do
	cd $dir
	echo ""
	echo "--------------------------------------------"
	echo "Building Sonar report for ${PWD##*/} "
	echo "--------------------------------------------"
	echo ""
	sonar-scanner -X -Dsonar.projectKey="${PWD##*/}" -Dsonar.sources=./src/main/java -Dsonar.java.binaries=./build/classes -Dsonar.host.url=http://localhost:9000/sonar -Dsonar.login=admin -Dsonar.password=$1 
	cd ..
done
cd ../../..

cd ../deploy/data/repos/4
echo "Build project Spring boot"
./gradlew build

cd spring-boot-project

directories=( "spring-boot" "spring-boot-autoconfigure" "spring-boot-devtools" "spring-boot-properties-migrator" "spring-boot-test-autoconfigure" \
"spring-boot-actuator" "spring-boot-cli" "spring-boot-docs" "spring-boot-starters" "spring-boot-tools" \
"spring-boot-actuator-autoconfigure" "spring-boot-dependencies" "spring-boot-parent" "spring-boot-test" )


for dir in "${directories[@]}"
do
	cd $dir
	echo ""
	echo "--------------------------------------------"
	echo "Building Sonar report for ${PWD##*/} "
	echo "--------------------------------------------"
	echo ""
	sonar-scanner -X -Dsonar.projectKey="${PWD##*/}" -Dsonar.sources=./src/main/java -Dsonar.java.binaries=./build/classes -Dsonar.host.url=http://localhost:9000/sonar -Dsonar.login=admin -Dsonar.password=$1 
	cd ..
done
