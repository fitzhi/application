#
# Tutorial used for the installation
# https://techexpert.tips/fr/sonarqube/installation-de-scanner-sonarqube-sur-ubuntu-linux/
#
mkdir /downloads -p
mkdir /downloads/sonarqube -p
cd /downloads/sonarqube
wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.6.0.2311-linux.zip
unzip sonar-scanner-cli-4.6.0.2311-linux.zip
sudo mv sonar-scanner-4.6.0.2311-linux /opt/sonar-scanner

# Then we add the export >> PATH="$PATH:/opt/sonar-scanner/bin" << in the .profile file
