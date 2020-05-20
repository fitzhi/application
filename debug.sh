#!/bin/sh
cd deploy/backend-fitzhi
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar fitzhi.jar
cd ../..

