# To generate
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass myTestingPassword
# To test
keytool -list -v -storetype pkcs12 -keystore keystore.p12
# To deploy in the resources folder
copy keystore.p12 ..\back-fitzhi\src\main\resources\keystore.p12