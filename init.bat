echo ""
echo "Building back-end Fitzhi"
echo "------------------------"
echo ""
mkdir git_repo_for_test
cd git_repo_for_test
rmdir /Q /s first-test
git clone https://github.com/frvidal/first-test 
rmdir /Q /s application
git clone https://github.com/fitzhi/application
rmdir /Q /s mock-repo-with-branches-for-dev-and-testing-purposes
git clone https://github.com/fitzhi/mock-repo-with-branches-for-dev-and-testing-purposes
rmdir /Q /s repo-test-number-of-lines
git clone https://github.com/fitzhi/repo-test-number-of-lines
cd ..
mkdir deploy\backend-fitzhi
mkdir deploy\data
cd back-fitzhi
call mvn clean install
echo ""
echo "Building back-end Fitzhi"
echo "------------------------"
copy .\target\fitzhi.jar ..\deploy\backend-fitzhi\fitzhi.jar
copy .\target\application.properties ..\deploy\backend-fitzhi\application.properties
copy .\target\logback-spring.xml ..\deploy\backend-fitzhi\logback-spring.xml
xcopy /E /I data ..\deploy\data
cd ..

