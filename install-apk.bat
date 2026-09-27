@echo off
setlocal
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

if not exist "%ADB%" (
    echo Erreur : adb.exe introuvable dans %LOCALAPPDATA%\Android\Sdk\platform-tools
    pause
    exit /b 1
)

set "TARGET=-d"
if not "%~1"=="" set "TARGET=-s %~1"

echo Verification des appareils connectes :
"%ADB%" devices
echo.

set "APK=fumper-android\build\outputs\apk\debug\fumper-android-debug.apk"
if not exist "%APK%" (
    echo L'APK n'existe pas encore. Lancement de la compilation...
    call build-apk.bat
)

echo Installation de %APK% sur l'appareil...
"%ADB%" %TARGET% install -r "%APK%"
if %ERRORLEVEL% equ 0 (
    echo.
    echo ============================================================
    echo Application installee avec succes !
    echo Lancement automatique sur le telephone...
    echo ============================================================
    "%ADB%" %TARGET% shell am start -n com.python4d.fumper/.MainActivity
) else (
    echo.
    echo Echec de l'installation.
    echo Pensez a verifier que le debogage USB est active et autorise sur votre smartphone.
)
endlocal
