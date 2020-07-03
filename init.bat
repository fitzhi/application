echo ""
echo "Building back-end Wibkac"
echo "------------------------"
echo ""
mkdir -pv git_repo_for_test
cd git_repo_for_test
rmdir /Q /s first-test
git clone https://github.com/frvidal/first-test 
rmdir /Q /s wibkac
git clone https://github.com/frvidal/wibkac
cd ..
mkdir deploy
mkdir deploy\backend-fitzhi
cd back-fitzhi
mvn package -DargLine="-Dfile_separator=\"
cp target\fitzhi.jar ..\deploy\backend-fitzhi\fitzhi.jar
cp -R data ..\deploy\data
cd ..

