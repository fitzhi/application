rm -rf deploy
./init.sh
mv deploy/data/referential/openid-servers.json deploy/data/referential/openid-servers-empty.json
mv deploy/data/referential/openid-servers-github-google-localhost-4200.json deploy/data/referential/openid-servers.json
./run.sh HTTP

