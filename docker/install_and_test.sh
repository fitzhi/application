export LANG=C.UTF-8
apt-get update
apt-get -y install maven
apt-get -y install default-jdk
apt-get -y install git


#
# Testing an alternative Open JDK
# apt-get wget 
# wget https://download.java.net/java/GA/jdk13.0.2/d4173c853231432d94f001e99d882ca7/8/GPL/openjdk-13.0.2_linux-x64_bin.tar.gz
# tar xvf openjdk-13.0.2_linux-x64_bin.tar.gz
# alias java=jdk-13.0.2/bin/java
# alias javac=jdk-13.0.2/bin/javac
# export PATH=$PATH:jdk-13.0.2/bin
#

git clone --branch release-1.4 https://github.com/fitzhi/application.git

cd application

./init.sh

