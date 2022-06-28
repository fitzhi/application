#!/bin/bash
java -Xmx1g -jar fitzhi-1.9-SNAPSHOT.jar --spring.profiles.active="slave, HTTP" > out.txt
