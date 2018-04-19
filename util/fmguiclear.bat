@echo off
cls
IF NOT EXIST %APPDATA%\Intel\FabricManagerGUI (
   ECHO Fabric Manager GUI database not found for this user!
   GOTO :EOF
)

ECHO WARNING: You are about to delete the Fabric Manager GUI database holding all of your host configuration settings!!!
SET /P ANSWER=Do you wish to proceed? (y/n) 
IF "%ANSWER%"=="y" GOTO CLEARCACHE
IF "%ANSWER%"=="Y" GOTO CLEARCACHE
GOTO :EOF

:CLEARCACHE
ECHO.
ECHO Deleting Fabric Manager GUI database...
ECHO DEL /s /q %APPDATA%\Intel\FabricManagerGUI\db\*
DEL /s /q %APPDATA%\Intel\FabricManagerGUI\db\* >nul
ECHO DEL /s /q %APPDATA%\Intel\FabricManagerGUI\logs\*
DEL /s /q %APPDATA%\Intel\FabricManagerGUI\logs\* >nul
ECHO Fabric Manager GUI database has been deleted! 
ECHO.
PAUSE