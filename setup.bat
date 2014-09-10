@echo off

mkdir temp && cd temp

rem Create empty database file
type NUL > db.sqlite


rem Generate mandatory files for SSL (key and cert)
echo.
echo Generate Private Key
echo ===============================
keytool -genkey -alias fspresenceserver -keyalg RSA -keystore server.jks

echo.
echo Export Certificate
echo ===============================
keytool -export -alias fspresenceserver -file server.cer -keystore server.jks

echo.
echo Import Certificate into Truststore
echo ===============================
keytool -import -v -trustcacerts -alias fspresenceserver -file server.cer -keystore client.jks

echo.
echo Import Certificate into Android Truststore
echo ===============================
keytool -importcert -v -trustcacerts -file "server.cer" -alias fspresenceserver -keystore "keystore.bks" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "..\misc\bcprov-jdk15on-146.jar" -storetype BKS


rem Distribute files
copy db.sqlite ..\FSPresenceServer\db.sqlite
mkdir ..\FSPresenceServer\ssl
copy server.jks ..\FSPresenceServer\ssl\server.jks
copy client.jks ..\FSPresencesClient\client.jks
copy keystore.bks ..\FSPresence\res\raw\keystore.bks

cd..
rmdir /S /q temp

pause
