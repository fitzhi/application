echo off
echo(
if "%1" == "HTTPS" (
	echo Starting backend Fitzhi in HTTPS mode
	copy .\back-fitzhi\src\main\resources\application-https.properties .\deploy\backend-fitzhi\application-https.properties
)
if "%1" == "HTTP" (
	echo Starting backend Fitzhi in HTTP mode
	del .\deploy\backend-fitzhi\application-https.properties
)
if NOT "%1" == "HTTP" (
	if NOT "%1" == "HTTPS" (
		echo Invalid profile given. Expecting either HTTP, or HTTPS.
	)
)
echo --------------------------------------
echo(
cd ./deploy/backend-fitzhi/
java -jar fitzhi.jar --spring.profiles.active=%1

