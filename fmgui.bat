@echo off
REM Runs the Fabric Manager GUI
SETLOCAL enabledelayedexpansion
SET DEBUG=0
SET JAVA_REQ=17
SET INSTDIR=%~dp0
SET JAVA=nojava
SET JAR=fmgui.jar

IF DEFINED OPA_JAVA (
    REM Check what's been provided in environment variable OPA_JAVA 
    call:checkJava JAVA "%OPA_JAVA%\bin\javaw.exe"
)
IF "%JAVA%" == "nojava" (
    REM No JVM found yet, try JAVA_HOME
    if DEFINED JAVA_HOME (
        call:checkJava JAVA "%JAVA_HOME%\bin\javaw.exe"
    )
)
IF "%JAVA%" == "nojava" (
    REM Either JAVA_HOME was not defined or the specified JVM is not suitable. Try the PATH 
    call:checkJava JAVA "javaw.exe"
)
IF "%JAVA%" == "nojava" (
    REM JVM is not defined in the PATH or is not suitable. Attempt to find one in %PROGRAMFILES%
    FOR /f "tokens=*" %%i in ('dir /b /s "%PROGRAMFILES%\javaw.exe"') do (
        call:checkJava JAVA "%%i"
        IF NOT "!JAVA!" == "java"  goto invokeApp
    )
)

:invokeApp
IF "%JAVA%" == "nojava" (
    ECHO Java version 1.7 or higher is required to run application Fabric Manager GUI
    ECHO Please, download Java version 1.7 or higher from Java's download site:
    ECHO
    ECHO               http://www.java.com/en/download/manual.jsp
    ECHO
    ECHO and install it in your system. The installer adds Java 1.7 to your PATH.  If
    ECHO you do not want to override your default Java version, leave your PATH as it
    ECHO is and set the environment variable OPA_JAVA to the installation location of
    ECHO the Java 1.7 runtime. To do this, go to Control Panel and select:
    ECHO 
    ECHO           System > Advanced system settings > Environment Variables...
    ECHO 
    ECHO Then rerun this command.
    ECHO 
    PAUSE
) ELSE (
    IF EXIST "%INSTDIR%\fmgui.jar" (
        SET JAR=%INSTDIR%\fmgui.jar
        REM Run the JVM and pass the jar name
        IF %DEBUG% EQU 1 ECHO Executing %JAVA% -jar !JAR!
        START "" /B "%JAVA%" -jar "!JAR!"
    ) ELSE (
        ECHO This script should be placed in the same location where the Fabric Manager GUI
        ECHO application was installed. Unable to find application jar file fmgui.jar
        ECHO 
        PAUSE
    )
)
goto:eof

:checkJava
IF %DEBUG% EQU 1 ECHO Checking JVM %~2
FOR /f eol^=J^ tokens^=1-5^ delims^=.-_^" %%j in ('^"%~2^" -version 2^>^&1') do (
    IF "%%j" == "java version " (
        IF %%k%%l GEQ %JAVA_REQ% (
            IF %DEBUG% EQU 1 ECHO Found suitable Java: %~2
            SET "%~1=%~2"
        )
    )
)
goto:eof
