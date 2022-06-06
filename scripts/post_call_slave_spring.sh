echo "Calling the slave for the Fitzhi project..."
echo "-------------------------------------------"
echo ""
curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/spring-projects/spring-framework"}' http://localhost:8081/api/project/analysis
echo "done"
echo ""
