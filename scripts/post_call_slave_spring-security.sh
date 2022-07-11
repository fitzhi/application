export FITZHI_URL=http://localhost:8081

echo "Calling the slave of the Fitzhi project to analyze the Spring security projet..."
echo "--------------------------------------------------------------------------------"
echo ""
curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/spring-projects/spring-security"}' "${FITZHI_URL}/api/project/analysis"
echo "done"
echo ""
