echo "Calling the slave for the First project..."
echo "------------------------------------------"
echo ""
curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/frvidal/first-test"}' http://localhost:8081/api/project/analysis
echo "done"
echo ""
