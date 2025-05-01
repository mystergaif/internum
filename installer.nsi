; Installer script for InterNum 3D Cube Application

; Define the name of the installer
Name "InterNum 3D Cube Application"
OutFile "InterNumApp_Standalone.exe"

; Default installation directory
InstallDir "$TEMP\InterNumApp"

; Request application privileges
RequestExecutionLevel user

; Set compression
SetCompressor /SOLID lzma

; Silent installer
SilentInstall silent
AutoCloseWindow true
ShowInstDetails hide

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

  ; Create directory
  SetOutPath "$INSTDIR"

  ; Copy JAR file
  File "target\internum-app-1.0-SNAPSHOT.jar"
  
  ; Create JRE directory
  CreateDirectory "$INSTDIR\jre"
  
  ; Copy JRE
  SetOutPath "$INSTDIR\jre"
  File /r "jre\windows\jdk-17.0.10+7-jre\*.*"
  
  ; Set working directory back to INSTDIR
  SetOutPath "$INSTDIR"

  ; Run the application directly (no batch file)
  ExecWait '"$INSTDIR\jre\bin\javaw.exe" -Xms128m -Xmx512m -jar "$INSTDIR\internum-app-1.0-SNAPSHOT.jar"'

  ; Clean up after the application is closed
  RMDir /r "$INSTDIR"
SectionEnd

; Set cursor back to normal on exit
Function .onGUIEnd
  System::Call 'user32::SetCursor(i 0)'
FunctionEnd
