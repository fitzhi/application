echo ""
echo "Building back-end Wibkac"
echo "------------------------"
echo ""
mkdir -pv git_repo_for_test
cd git_repo_for_test
rmdir /s first-test
git clone https://github.com/frvidal/first-test 
rmdir /s wibkac
git clone https://github.com/frvidal/wibkac
cd ..
mkdir -pv deploy/backend-wibkac
cd back-wibkac
mvn package
cp target\wibkac.jar ..\deploy\backend-wibkac\wibkac.jar
cp -R data ..\deploy\data
cd ..

