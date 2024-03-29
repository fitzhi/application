#!/bin/sh

helpFunction()
{
	echo ""
	echo "Usage: $0 [-f Y/N] [-d inst_dir] [-t Y/N] [-?]"
	echo -e "\t-f (Y/N) force mode is ON. Script will erase all data in the installation directory"
	echo -e "\t-d set the installation directory. Default is 'deploy'"
	echo -e "\t-t (Y/N) activate or inactivate the test"
	echo -e "\t-? display this Help message before the installation"
	exit 1 # Exit script after printing help
}

while getopts "f:d:t:?" opt
do
	case "$opt" in
		f) force="$OPTARG";;
		d) dir="$OPTARG";;
		t) test="$OPTARG";;
		? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
	esac
done

export VERSION_FITZHI=`cat back-fitzhi/VERSION_FITZHI`
echo "Initializing the Fitzhì backend for the release ${VERSION_FITZHI}"
echo " The setup will erase & override previous installation if the FORCE mode is on"


if [ $test = "Y" ]
then 
echo "   1) Building and testing the fitzhi back-end"
else 
echo "   1) Building the fitzhi back-end"
fi

echo "   2) Testing the fitzhi back-end"
echo "   3) Deploy the spring-boot fitzhi back-end"
if [ $force = "Y" ]
then 
echo "   4) Copy the initial data"
fi
echo "------------------------"
echo ""

# We test with the testing repo only if we have chosen to maven with testing units.
if [ $test = "Y" ]
then 
mkdir -pv git_repo_for_test
cd git_repo_for_test
rm -rf first-test
git clone https://github.com/frvidal/first-test 
rm -rf application
git clone https://github.com/fitzhi/application
rm -rf mock-repo-with-branches-for-dev-and-testing-purposes
git clone https://github.com/fitzhi/mock-repo-with-branches-for-dev-and-testing-purposes
rm -rf repo-test-number-of-lines
git clone https://github.com/fitzhi/repo-test-number-of-lines
cd ..
fi

mkdir -pv $dir/backend-fitzhi
cd back-fitzhi

if [ $test = "Y" ]
then 
mvn clean install
else 
mvn clean install -Dmaven.test.skip=true
fi

cp target/fitzhi-${VERSION_FITZHI}.jar ../$dir/backend-fitzhi/fitzhi-${VERSION_FITZHI}.jar
cp target/application.properties ../$dir/backend-fitzhi/application.properties
cp target/logback-spring.xml ../$dir/backend-fitzhi/logback-spring.xml

# docker & nginx settings
rm -rf ../$dir/docker
cp -R ../docker/docker ../$dir

if [ $force = "Y" ]
then 
cp -R data ../$dir
fi

cd ..

echo "--- end ---"
