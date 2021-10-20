#!/bin/sh

helpFunction()
{
	echo ""
	echo "Usage: $0 [-d inst_dir] [-t Y/N] [-h]"
	echo "\t-d set the installation directory. Default is 'deploy'"
	echo "\t-t (Y/N) activate or inactivate the test"
	echo "\t-h display this Help message before the installation"
	echo ""
	exit 1 # Exit script after printing help
}

while getopts "d:t:h" opt
do
	case "$opt" in
		d) dir="$OPTARG";;
		t) test="$OPTARG";;
		? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
	esac
done

if [ -z "$dir" ] || [ -z "$dir" ]
then
   echo "Some or all of the parameters are empty"
   helpFunction
fi

echo "Initializing the Fitzhì Angular Frontend"

if [ $test = "Y" ]
then 
echo "   1) Building and Testing the fitzhi front-end"
else 
echo "   1) Building the fitzhi front-end"
fi

echo "   2) Testing the fitzhi front-end"
echo "   -------------------------------"
# We test the application before the generation.
if [ $test = "Y" ]
then 
cd ..
npm run test
cd -
fi

echo "   3) Building the fitzhi front-end"
echo "   --------------------------------"
cd ..
sh ./gen-build-ts.sh
npm run build-prod-docker
cd -

rm -f $dir/../front-fitzhi.zip
rm -rf $dir
mv ../dist-docker $dir

tar -zcvf $dir/../front-fitzhi.zip $dir

echo "--- end ---"
