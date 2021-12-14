apt update -y
apt-get install language-pack-fr
update-locale LANG=fr_FR.utf8
apt install openjdk-11-jre -y
apt-get install maven -y
apt install git -y
git clone --branch release-1.7 https://github.com/fitzhi/application.git
cd application
./init.sh

