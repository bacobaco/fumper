@echo off
setlocal
if not defined JAVA_HOME (
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
    )
)
echo Lancement de Fumper...
call gradlew.bat :fumper-desktop:run
endlocal
