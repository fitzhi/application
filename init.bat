echo ""
echo "Building back-end Fitzhi"
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
mkdir deploy\data
cd back-fitzhi
call mvn install -DargLine="-Dfile_separator=\\ -Dfile.encoding=\"UTF-8\" -Dsun.jnu.encoding=\"UTF-8\""
echo ""
echo "Building back-end Fitzhi"
echo "------------------------"
xcopy .\target\fitzhi.jar ..\deploy\backend-fitzhi\fitzhi.jar
xcopy .\target\application.properties ..\deploy\backend-fitzhi\application.properties
xcopy .\target\logback-spring.xml ..\deploy\backend-fitzhi\logback-spring.xml
xcopy /E /I data ..\deploy\data
cd ..

