; Installer script for InterNum 3D Cube Application with error handling

; Define the name of the installer
Name "InterNum 3D Cube Application"
OutFile "InterNumApp_Standalone_Debug.exe"

; Default installation directory
InstallDir "$TEMP\InterNumApp"

; Request application privileges
RequestExecutionLevel user

; Set compression
SetCompressor /SOLID lzma

; Silent installer
SilentInstall silent
AutoCloseWindow true
ShowInstDetails show

; Modern UI
!include "MUI2.nsh"

; No pages
!insertmacro MUI_PAGE_INSTFILES

; Language
!insertmacro MUI_LANGUAGE "English"

; The stuff to install
Section "Main Application" SecMain
  ; Set cursor to wait
  System::Call 'user32::SetCursor(i $HWNDPARENT)'

  ; Create log file
  FileOpen $0 "$TEMP\internum_installer_log.txt" w
  FileWrite $0 "Installation started at $\r$\n"
  FileWrite $0 "Temp directory: $TEMP$\r$\n"
  FileWrite $0 "Install directory: $INSTDIR$\r$\n"
  FileClose $0

  ; Create directory
  SetOutPath "$INSTDIR"
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Created install directory$\r$\n"
  FileClose $0

  ; Copy JAR file
  File "target\internum-app-1.0-SNAPSHOT.jar"
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Copied JAR file$\r$\n"
  FileClose $0
  
  ; Create JRE directory
  CreateDirectory "$INSTDIR\jre"
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Created JRE directory$\r$\n"
  FileClose $0
  
  ; Copy JRE
  SetOutPath "$INSTDIR\jre"
  File /r "jre\windows\jdk-17.0.10+7-jre\*.*"
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Copied JRE files$\r$\n"
  FileClose $0
  
  ; Set working directory back to INSTDIR
  SetOutPath "$INSTDIR"
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Set working directory to $INSTDIR$\r$\n"
  FileWrite $0 "Launching application...$\r$\n"
  FileClose $0

  ; Create a batch file to run the application and capture errors
  FileOpen $0 "$INSTDIR\run_app.bat" w
  FileWrite $0 '@echo off$\r$\n'
  FileWrite $0 'cd /d "%~dp0"$\r$\n'
  FileWrite $0 'echo Running application... > "%TEMP%\internum_app_log.txt"$\r$\n'
  FileWrite $0 'jre\bin\javaw.exe -Xms128m -Xmx512m -jar internum-app-1.0-SNAPSHOT.jar 2>> "%TEMP%\internum_app_log.txt"$\r$\n'
  FileWrite $0 'if %ERRORLEVEL% NEQ 0 ($\r$\n'
  FileWrite $0 '  echo Application exited with error code %ERRORLEVEL% >> "%TEMP%\internum_app_log.txt"$\r$\n'
  FileWrite $0 '  start notepad "%TEMP%\internum_app_log.txt"$\r$\n'
  FileWrite $0 ')$\r$\n'
  FileClose $0

  ; Run the application
  ExecWait '"$INSTDIR\run_app.bat"'
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Application closed$\r$\n"
  FileWrite $0 "Cleaning up...$\r$\n"
  FileClose $0

  ; Clean up after the application is closed
  RMDir /r "$INSTDIR"
  
  ; Log step
  FileOpen $0 "$TEMP\internum_installer_log.txt" a
  FileWrite $0 "Cleanup complete$\r$\n"
  FileClose $0
SectionEnd

; Set cursor back to normal on exit
Function .onGUIEnd
  System::Call 'user32::SetCursor(i 0)'
FunctionEnd
