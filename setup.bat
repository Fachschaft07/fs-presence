@echo off

rem Create empty database file
type NUL > FSPresenceServer\db.sqlite


rem Generate mandatory files for SSL (key and cert)
mkdir temp && cd temp
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

mkdir ..\FSPresenceServer\ssl
copy server.jks ..\FSPresenceServer\ssl\server.jks
copy client.jks ..\FSPresencesClient\client.jks

cd..
rmdir /S /q temp