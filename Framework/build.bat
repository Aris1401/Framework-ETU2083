cd ./build/web/WEB-INF/classes
jar cf ./framework.jar ./etu2083
# mkdir /home/aris/S4-ITU/Framework-ETU2083/Test-Framework/web/WEB-INF/lib
copy "U:\S4-ITU\Framework-ETU2083\Framework\build\web\WEB-INF\classes\framework.jar" "U:\S4-ITU\Framework-ETU2083\Test-Framework\web\WEB-INF\lib"
copy "U:\S4-ITU\Framework-ETU2083\Framework\build\web\WEB-INF\classes\framework.jar" "U:\S4-ITU\Framework-ETU2083\Test-Framework\build\web\WEB-INF\lib"
cd "U:\S4-ITU\Framework-ETU2083\Test-Framework\build\web"
jar cf ./test-framework.war .
copy "U:\S4-ITU\Framework-ETU2083\Test-Framework\build\web\test-framework.war" "U:\apache-tomcat-8.5.87\webapps"
cd "U:\S4-ITU\Framework-ETU2083\Framework"
