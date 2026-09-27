@echo off
setlocal
if not defined JAVA_HOME (
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
    )
)
echo Compilation de l'APK Fumper...
call gradlew.bat :fumper-android:assembleDebug
echo.
echo ============================================================
echo APK genere avec succes :
echo fumper-android\build\outputs\apk\debug\fumper-android-debug.apk
echo ============================================================
endlocal
