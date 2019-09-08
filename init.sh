echo ""
echo "Building back-end Wibkac"
echo "------------------------"
echo ""
mkdir -pv git_repo_for_test
cd git_repo_for_test
rm -Rf first-test
git clone https://github.com/frvidal/first-test 
rm -Rf wibkac
git clone https://github.com/frvidal/wibkac
cd ..
mkdir -pv deploy/backend-wibkac
cd back-wibkac
mvn clean package
cp target/wibkac.jar ../deploy/backend-wibkac/wibkac.jar
cp -R data ../deploy/data
cd ..

