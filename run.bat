echo off
echo(
if /i "%1" == "https" (
    echo Starting backend Fitzhi in HTTPS mode
    copy .\back-fitzhi\src\main\resources\application-https.properties .\deploy\backend-fitzhi\application-https.properties
)
if /i "%1" == "http" (
    echo Starting backend Fitzhi in HTTP mode
    del .\deploy\backend-fitzhi\application-https.properties
)
if NOT "%1" == "http" (
    if NOT "%1" == "https" (
        echo Invalid profile given. Expecting either HTTP, pr HTTPS
    )
)
echo --------------------------------------
echo(
cd ./deploy/backend-fitzhi/
java -jar fitzhi.jar --spring.profiles.active=%1

