@echo off
setlocal enabledelayedexpansion

echo ========================================
echo   BUILD + RUN Main (framework + projetTest)
echo ========================================
echo.

REM 1) Build framework JAR
echo [1/4] Building frameworkAnja JAR...
call mvn -f "frameworkAnja\pom.xml" clean package -q
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec build framework
    exit /b 1
)

set "FRAMEWORK_JAR=frameworkAnja\target\framework-anja-1.0.0.jar"
if not exist "%FRAMEWORK_JAR%" (
    echo ERREUR: JAR introuvable: %FRAMEWORK_JAR%
    exit /b 1
)
echo ✓ Framework JAR pret: %FRAMEWORK_JAR%

REM 2) Optional: copy JAR into test WEB-INF/lib (for IDE or WAR packaging)
echo [2/4] Copy framework JAR into test WEB-INF/lib...
if not exist "projetTest\src\main\webapp\WEB-INF\lib" mkdir "projetTest\src\main\webapp\WEB-INF\lib"
copy /Y "%FRAMEWORK_JAR%" "projetTest\src\main\webapp\WEB-INF\lib\framework-anja-1.0.0.jar" >nul
echo ✓ JAR copie

REM 3) Compile projetTest classes against framework JAR
echo [3/4] Compiling projetTest sources...
set "OUT_DIR=projetTest\target\classes"
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

REM Add here all Main/Test classes you want to compile
set "SRC_TEST=projetTest\src\main\java\test\Main.java projetTest\src\main\java\test\ProductController.java"

javac -cp "%FRAMEWORK_JAR%" -d "%OUT_DIR%" %SRC_TEST%
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec compilation projetTest
    exit /b 1
)
echo ✓ Compilation OK: %OUT_DIR%

REM 4) Run Main
echo [4/4] Running test.Main ...
java -cp "%OUT_DIR%;%FRAMEWORK_JAR%" test.Main
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Execution de Main a echoue
    exit /b %ERRORLEVEL%
)
echo ✓ Terminé
pause
