@echo off
setlocal
cd /d "%~dp0"

echo ============================================================
echo   Compilation de FUMPER HTML5 (GWT to JavaScript)
echo ============================================================
echo.

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
call gradlew.bat :fumper-html:distHtml

if %ERRORLEVEL% equ 0 (
    echo.
    echo ============================================================
    echo   Compilation terminee avec succes !
    echo   Dossier exporte : fumper-html\dist
    echo ============================================================
) else (
    echo.
    echo   Erreur lors de la compilation.
)
pause
