#!/bin/sh

# Setup here the path of maven
alias mvn=/usr/local/maven/bin/mvn

helpFunction()
{
   echo ""
   echo "Usage: $0 [-f Y/N] [-d inst_dir] [-?]"
   echo -e "\t-f force mode is ON. Script will erase all data in the installation directory"
   echo -e "\t-d set the installation directory. Default is 'deploy'"
   echo -e "\t-? display this Help message before the installation"
   exit 1 # Exit script after printing help
}

while getopts "f:d:?" opt
do
   case "$opt" in
      f) force="$OPTARG";;
      d) dir="$OPTARG";;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

echo "Initializing the Techxhì backend"
echo " The setup will erase & override previous installation if the FORCE mode is on"

echo "   1) Building the Tixhì back-end"
echo "   2) Testing the Tixhì back-end"
echo "   3) Deploy the spring-boot Tixhì back-end"
if [ $force = "Y" ]
then 
echo "   4) Copy the initial data"
fi
echo "------------------------"
echo ""

mkdir -pv git_repo_for_test
cd git_repo_for_test
rm -Rf first-test
git clone https://github.com/frvidal/first-test 
rm -Rf wibkac
git clone https://github.com/frvidal/wibkac
cd ..
mkdir -pv $dir/backend-wibkac
cd back-wibkac
mvn clean package
cp target/wibkac.jar ../$dir/backend-wibkac/wibkac.jar

if [ $force = "Y" ]
then 
cp -R data ../$dir
fi

cd ..
