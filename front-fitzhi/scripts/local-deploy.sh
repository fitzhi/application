echo "Building the Fitzhi Angular application and local copy."
echo "-------------------------------------------------------"
rm  -rf  /var/www/fitzhi
mkdir /var/www/fitzhi
cd ..
sh ./gen-build-ts.sh
npm run prod-var-www-fithi
