echo "Calling the slave of the Fitzhi project to analyze the Spring security projet..."
echo "--------------------------------------------------------------------------------"
echo ""
curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/spring-projects/spring-security"}' http://localhost:8081/api/project/analysis
echo "done"
echo ""
