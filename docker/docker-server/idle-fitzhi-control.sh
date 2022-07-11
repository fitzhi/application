#!/bin/bash

MPHR=60    # Minutes per hour.

stat -c '%Y' "./spring-boot-logger.log"
LOG_lastModified=$(date '+%s' -d "@$( stat -c '%Y' "./spring-boot-logger.log"; )")
echo $LOG_lastModified
MINUTES=0
while [[ $MINUTES < 10 ]]
do
  NOW=$(date '+%s')
  echo $NOW
  MINUTES=$(( ($NOW - $LOG_lastModified) / $MPHR ))
  echo $MINUTES
  sleep 1m
done
kill $(cat ./pid.file)
echo "Server termination!"

