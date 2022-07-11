
export FITZHI_URL=http://localhost:8081

echo "Calling the slave of Fitzhi for the Spring project..."
echo "-----------------------------------------------------"
echo ""
curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/spring-projects/spring-boot.git", "branch": "main"}' "${FITZHI_URL}/api/project/analysis"
echo "done"
echo ""
