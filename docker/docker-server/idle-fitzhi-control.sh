#!/bin/bash

echo " * Starting fitzhi idle control with an idle timeout limit of " $idle_timeout_limit

MPHR=60    # Minutes per hour.

sleep 2m
MINUTES=0


while [[ $MINUTES -lt $idle_timeout_limit ]]
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

