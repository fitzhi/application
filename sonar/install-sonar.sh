echo "Sonar echosystem installation"
echo "-----------------------------"
echo ""

mkdir /downloads/sonarqube -p
cd /downloads/sonarqube
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-8.7.0.41497.zip
unzip sonarqube-8.7.0.41497.zip
mv sonarqube-8.7.0.41497 /opt/sonarqube

mkdir /downloads/sonarqube -p
cd /downloads/sonarqube
wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.6.0.2311-linux.zip
unzip sonar-scanner-cli-4.6.0.2311-linux.zip
mv sonar-scanner-4.6.0.2311-linux /opt/sonar-scanner/bin