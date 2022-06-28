#!/bin/bash
java -Xmx1g -jar fitzhi-1.9-SNAPSHOT.jar --spring.profiles.active="slave, HTTP" > out.txt

# Terminating the container from inside.
# cf. https://stackoverflow.com/questions/31538314/stopping-docker-container-from-inside
trap "exit" SIGINT SIGTERM
kill -s SIGINT 1.
