@echo off

set "frameworkPath=U:\S4-ITU\Framework-ETU2083\Test-Framework"
set "webappsPath=U:\apache-tomcat-8.5.87\webapps"

cd .\build\web\WEB-INF\classes
jar cf .\framework.jar .\etu2083
mkdir "%frameworkPath%\web\WEB-INF\lib"
xcopy /Y .\framework.jar "%frameworkPath%\web\WEB-INF\lib"
xcopy /Y .\framework.jar "%frameworkPath%\build\web\WEB-INF\lib"
cd "%frameworkPath%\build\web"
jar cf .\test-framework.war .\*
move .\test-framework.war "%webappsPath%"
cd U:\S4-ITU\Framework-ETU2083\Framework
