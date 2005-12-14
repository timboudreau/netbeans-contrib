set PATH=%PATH%;C:\Programme\Microsoft Visual C++ Toolkit 2003\bin
set INCLUDE=C:\Programme\Microsoft Visual C++ Toolkit 2003\include;C:\Programme\Microsoft Platform SDK\Include;C:\Programme\j2sdk1.4.2_09\include;C:\Programme\j2sdk1.4.2_09\include\win32
set LIB=C:\Programme\Microsoft Platform SDK\Lib;C:\Programme\Microsoft Visual C++ Toolkit 2003\lib;C:\Programme\j2sdk1.4.2_09\lib

cl.exe /LD org_netbeans_modules_tasklist_timerwin_AlwaysOnTop.cpp jawt.lib user32.lib
pause