#!/bin/bash
echo "slave will connect with ${applicationUrl}."
java -jar fitzhi-1.9-SNAPSHOT.jar --spring.profiles.active="slave, HTTP"
