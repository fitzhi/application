#!/bin/sh

echo "Creating Admin User"
echo "-------------------"
echo ""
curl -X POST  -H "Content-Type: application/json" -d '{"login": "myFakeUser", "password": "myFakePassword" }'  http://localhost:8080/api/admin/classic/primeRegister


echo "Connecting Admin User"
echo "---------------------"
echo ""
curl -v -X POST  -u fitzhi-trusted-client:secret -H "Content-Type: application/x-www-form-urlencoded" -d "username=myFakeUser&password=myFakePassword&grant_type=password"  http://localhost:8080/oauth/token > out.json

export ACCESS_TOKEN=$(jq .access_token out.json)
export ACCESS_TOKEN=$(echo $ACCESS_TOKEN | cut -d "\"" -f 2)

echo $ACCESS_TOKEN 
echo "------------------------"

rm out.json

echo "Creating First Project"
echo "----------------------"
echo ""
curl -v -H "Content-Type:  application/json" -H "Authorization: Bearer ${ACCESS_TOKEN}"  -d '{"name": "First test", "connectionSettings": "3", "branch": "master", "urlRepository": "https://github.com/frvidal/first-test"}' http://localhost:8080/api/project

echo "Creating Spring Framework Project"
echo "---------------------------------"
echo ""
curl -v -H "Content-Type:  application/json" -H "Authorization: Bearer ${ACCESS_TOKEN}"  -d '{"name": "Spring Framework", "connectionSettings": "3", "branch": "5.3.x", "urlRepository": "https://github.com/spring-projects/spring-framework"}' http://localhost:8080/api/project

echo "Creating my User"
echo "----------------"
echo ""
# curl -v -H "Content-Type:  application/json" -H "Authorization: Bearer ${ACCESS_TOKEN}"  -d '{"firstName":"Frédéric", "lastName":"Vidal", "login": "frvidal", "nickName":"altF4", "email":"frvidal@nope.com"}' http://localhost:8080/api/staff

echo "Creating Spring Security Project"
echo "---------------------------------"
echo ""
curl -v -H "Content-Type:  application/json" -H "Authorization: Bearer ${ACCESS_TOKEN}"  -d '{"name": "Spring Security", "connectionSettings": "3", "branch": "5.8.x", "urlRepository": "https://github.com/spring-projects/spring-security"}' http://localhost:8080/api/project

echo "Creating Spring Security Oauth Project"
echo "--------------------------------------"
echo ""
curl -v -H "Content-Type:  application/json" -H "Authorization: Bearer ${ACCESS_TOKEN}"  -d '{"name": "Spring Security Oauth", "connectionSettings": "3", "branch": "main", "urlRepository": "https://github.com/spring-projects/spring-security-oauth"}' http://localhost:8080/api/project


