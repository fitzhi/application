echo "Building back-end Wibkac"
echo "------------------------"
echo ""
mkdir -pv deploy/backend-wibkac
cd back-wibkac
mvn package
cp target/wibkac.jar ../deploy/backend-wibkac/wibkac.jar
cp -R data ../deploy/data
cd ..

