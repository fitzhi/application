#!/bin/sh

export FITZHI_URL=http://spoq.fitzhi.com:8082

echo "Creating Admin User"
echo "-------------------"
echo ""
curl -X POST  -H "Content-Type: application/json" -d '{"login": "myFakeUser", "password": "myFakePassword" }'  "${FITZHI_URL}/api/admin/classic/primeRegister"


echo "Connecting very first Admin User"
echo "--------------------------------"
echo ""
curl -X POST  "${FITZHI_URL}/api/admin/saveVeryFirstConnection"

