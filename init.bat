echo ""
echo "Building back-end Wibkac"
echo "------------------------"
echo ""
mkdir -pv git_repo_for_test
cd git_repo_for_test
rmdir /Q /s first-test
git clone https://github.com/frvidal/first-test 
rmdir /Q /s application
git clone https://github.com/fitzhi/application
cd ..
mkdir deploy\backend-fitzhi
cd back-fitzhi
mvn package -DargLine="-Dfile_separator=\\ -Dfile.encoding=\"UTF-8\" -Dsun.jnu.encoding=\"UTF-8\""
copy .\target\fitzhi.jar ..\deploy\backend-fitzhi\fitzhi.jar
copy .\target\application.properties ..\deploy\backend-fitzhi\application.properties
copy .\target\logback-spring.xml ..\deploy\backend-fitzhi\logback-spring.xml
cd ..

