#!/bin/bash

export FITZHI_URL=http://localhost:8080

echo "Creating Admin User"
echo "-------------------"
echo ""
curl -X POST  -H "Content-Type: application/json" -d '{"login": "myFakeUser", "password": "myFakePassword" }' "${FITZHI_URL}/api/admin/classic/primeRegister"
echo "Connecting Admin User"
echo "---------------------"
echo ""
curl -v -X POST  -u fitzhi-trusted-client:secret -H "Content-Type: application/x-www-form-urlencoded" -d "username=myFakeUser&password=myFakePassword&grant_type=password" "${FITZHI_URL}/oauth/token" > out.json

export ACCESS_TOKEN=$(jq .access_token out.json)
export ACCESS_TOKEN=$(echo $ACCESS_TOKEN | cut -d "\"" -f 2)

echo $ACCESS_TOKEN 
echo "------------------------"

rm out.json

echo "Shutdown the server   "
echo "----------------------"
echo ""
curl -X POST -H "Authorization: Bearer ${ACCESS_TOKEN}"  "${FITZHI_URL}/actuator/shutdown"

