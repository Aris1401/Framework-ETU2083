cd ./build/web/WEB-INF/classes
jar cf ./framework.jar ./etu2083
mkdir /home/aris/S4-ITU/Framework-ETU2083/Test-Framework/web/WEB-INF/lib
cp ./framework.jar /home/aris/S4-ITU/Framework-ETU2083/Test-Framework/web/WEB-INF/lib
cp ./framework.jar /home/aris/S4-ITU/Framework-ETU2083/Test-Framework/build/web/WEB-INF/lib
cd /home/aris/S4-ITU/Framework-ETU2083/Test-Framework/build/web
jar cf ./test-framework.war ./*
mv ./test-framework.war /home/aris/apache-tomcat-8.5.85/webapps
/home/aris/apache-tomcat-8.5.85/bin/startup.sh
