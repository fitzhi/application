rm -rf deploy
./init.sh
mv deploy/data/referential/openid-servers.json deploy/data/referential/openid-servers-empty.json
mv deploy/data/referential/openid-servers-github-google.json deploy/data/referential/openid-servers.json
./run.sh HTTP

