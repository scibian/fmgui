@ECHO OFF
SET THISDIR=%~dp0
For /f "tokens=2,*" %%G in ('REG QUERY "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" /v "Programs" ^|Find "REG_"') do Call Set STARTMENU=%%H
ECHO Copying %THISDIR%\fmguiclear.bat to %APPDATA%\Intel\FabricManagerGUI
COPY /V/Y "%THISDIR%\fmguiclear.bat" "%APPDATA%\Intel\FabricManagerGUI"
ECHO Copying %THISDIR%\Clear FM GUI Cache.lnk to %STARTMENU%\Intel\Omni-Path
MD "%STARTMENU%\Intel"
MD "%STARTMENU%\Intel\Omni-Path"
COPY /V/Y "%THISDIR%\Clear FM GUI Cache.lnk" "%STARTMENU%\Intel\Omni-Path"
