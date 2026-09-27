@echo off
setlocal
cd /d "%~dp0"

echo ============================================================
echo   Lancement de FUMPER en version Web HTML5
echo ============================================================
echo.

set "DIST_INDEX=fumper-html\dist\index.html"
if not exist "%DIST_INDEX%" (
    echo Compilation initiale du projet HTML5...
    set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
    call gradlew.bat :fumper-html:distHtml
    if errorlevel 1 (
        echo.
        echo Erreur lors de la compilation GWT / HTML5.
        pause
        exit /b 1
    )
)

echo Demarrage du serveur web local sur le port 8085...
start /b python -m http.server 8085 --directory fumper-html\dist

echo Ouverture du navigateur sur http://localhost:8085/ ...
timeout /t 1 /nobreak >nul
start http://localhost:8085/

echo.
echo Le jeu tourne dans votre navigateur !
echo Appuyez sur une touche pour quitter ce script.
echo ============================================================
pause
