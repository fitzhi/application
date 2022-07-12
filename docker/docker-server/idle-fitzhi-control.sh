#!/bin/bash

echo " * starting fitzhi idle control"

MPHR=60    # Minutes per hour.

sleep 2m
MINUTES=0

while [[ $MINUTES -lt $IDLE_TIMEOUT_LIMIT ]]
do
  LOG_lastModified=$(date '+%s' -d "@$( stat -c '%Y' "./logs/spring-boot-logger.log"; )")
  echo $LOG_lastModified
  NOW=$(date '+%s')
  echo $NOW
  MINUTES=$(( ($NOW - $LOG_lastModified) / $MPHR ))
  echo $MINUTES
  sleep 1m
done

kill $(cat ./pid.file)
echo " * fitzhi termination!"

