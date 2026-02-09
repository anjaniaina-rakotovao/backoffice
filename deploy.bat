@echo off
echo ========================================
echo   DEPLOIEMENT AUTOMATIQUE DU FRAMEWORK
echo ========================================
echo.

:: Configuration des chemins
set FRAMEWORK_DIR=frameworkAnja
set TEST_DIR=projetMrNaina
set TOMCAT_HOME=C:\Apache Software Foundation\Tomcat 10.1
set TOMCAT_DIR=%TOMCAT_HOME%\webapps
set FRAMEWORK_JAR=framework-anja-1.0.0.jar
set TEST_WAR=projetMrNaina-1.0.0.war
set APP_NAME=projetMrNaina-1.0.0

echo [1/4] Compilation du framework...
cd "%FRAMEWORK_DIR%"
call mvn clean package -q
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de la compilation du framework
    pause
    exit /b 1
)
echo ✓ Framework compile avec succes

echo.
echo [1.5/4] Installation du framework JAR dans le depot local Maven...
call mvn install:install-file ^
    -Dfile=target\%FRAMEWORK_JAR% ^
    -DgroupId=servlet ^
    -DartifactId=framework-anja ^
    -Dversion=1.0.0 ^
    -Dpackaging=jar -q
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de l'installation du JAR dans le depot local.
    pause
    exit /b 1
)
echo ✓ JAR installe localement.

echo.
echo [2/4] Copie du JAR du framework dans le projet de test...
cd ..
if not exist "%TEST_DIR%\src\main\webapp\WEB-INF\lib" (
    mkdir "%TEST_DIR%\src\main\webapp\WEB-INF\lib"
)

copy "%FRAMEWORK_DIR%\target\%FRAMEWORK_JAR%" "%TEST_DIR%\src\main\webapp\WEB-INF\lib\%FRAMEWORK_JAR%" >nul
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de la copie du JAR
    echo Verifiez que le fichier %FRAMEWORK_DIR%\target\%FRAMEWORK_JAR% existe
    pause
    exit /b 1
)
echo ✓ JAR copie dans le projet de test

echo.
echo [3/4] Compilation du projet de test (simple, sans dependance Maven)...
cd "%TEST_DIR%"
call mvn clean package -q
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de la compilation du projet de test
    echo Tentative avec compilation verbose...
    call mvn clean package
    pause
    exit /b 1
)
echo ✓ Projet de test compile avec succes

cd ..

echo.
echo [4/4] Deploiement sur Tomcat...

:: Suppression de l'ancien deploiement
if exist "%TOMCAT_DIR%\%TEST_WAR%" (
    del "%TOMCAT_DIR%\%TEST_WAR%" >nul 2>&1
    echo ✓ Ancien WAR supprime
)
if exist "%TOMCAT_DIR%\%APP_NAME%" (
    rmdir /s /q "%TOMCAT_DIR%\%APP_NAME%" >nul 2>&1
    echo ✓ Ancien dossier deploye supprime
)

:: Attendre un peu pour que les fichiers soient liberes
timeout /t 2 /nobreak >nul

:: Verifier que le WAR existe avant de le copier
if not exist "%TEST_DIR%\target\%TEST_WAR%" (
    echo ERREUR: Le fichier WAR n'existe pas: %TEST_DIR%\target\%TEST_WAR%
    pause
    exit /b 1
)

:: Copie du nouveau WAR avec verification
echo Copie de %TEST_DIR%\target\%TEST_WAR% vers %TOMCAT_DIR%\
copy "%TEST_DIR%\target\%TEST_WAR%" "%TOMCAT_DIR%\" >nul
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de la copie du WAR vers Tomcat
    echo Source: %TEST_DIR%\target\%TEST_WAR%
    echo Destination: %TOMCAT_DIR%\%TEST_WAR%
    echo Verifiez:
    echo - Que le dossier Tomcat existe: %TOMCAT_DIR%
    echo - Que vous avez les droits d'ecriture
    pause
    exit /b 1
)

:: Verification finale
if exist "%TOMCAT_DIR%\%TEST_WAR%" (
    echo ✓ WAR copie avec succes dans Tomcat webapps
    echo ✓ Fichier: %TOMCAT_DIR%\%TEST_WAR%
) else (
    echo ERREUR: Le WAR n'a pas ete copie correctement
    pause
    exit /b 1
)

echo.
echo ========================================
echo      DEPLOIEMENT TERMINE AVEC SUCCES!
echo ========================================
echo.
echo Le WAR a ete deploye avec succes dans Tomcat.
echo Demarrez Tomcat manuellement pour tester l'application.
echo.
echo L'application sera disponible sur:
echo http://localhost:8081/%APP_NAME%/
echo.
echo Pages de test disponibles:
echo - http://localhost:8081/%APP_NAME%/index.html

echo Fichier deploye: %TOMCAT_DIR%\%TEST_WAR%
echo.
pause