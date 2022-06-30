echo "Calling the slave for the Fitzhi project..."
echo "-------------------------------------------"
echo ""
curl -v -X PUT -H "Content-Type:  application/json"  -d '{"urlRepository": "https://github.com/hibernate/hibernate-orm.git"}' http://localhost:8081/api/project/analysis
echo "done"
echo ""
