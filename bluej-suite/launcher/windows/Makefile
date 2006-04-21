# -*- Makefile -*-
#
#                 Sun Public License Notice
# 
# The contents of this file are subject to the Sun Public License
# Version 1.0 (the "License"). You may not use this file except in
# compliance with the License. A copy of the License is available at
# http://www.sun.com/
# 
# The Original Code is Forte for Java, Community Edition. The Initial
# Developer of the Original Code is Sun Microsystems, Inc. Portions
# Copyright 1997-2005 Sun Microsystems, Inc. All Rights Reserved.
#

LIBS =
#LIBS = advapi32.lib user32.lib

CFLAGS = -s -mno-cygwin
#!ifdef DEBUG
#	CFLAGS = $(CFLAGS) -Zi
#!endif

all : nbbluej.exe nbbluej_w.exe

nbbluej.exe : nbbluej.cpp nbbluej.res
	gcc $(CFLAGS) $(LIBS) -mwindows -DWINMAIN nbbluej.cpp -o nbbluej.exe nbbluej.res

nbbluej_w.exe : nbbluej.cpp
	gcc $(CFLAGS) $(LIBS) nbbluej.cpp -o nbbluej_w.exe

nbbluej.res : nbbluej.rc
	windres -o$@ -Ocoff nbbluej.rc
  
clean :
	-rm *.res *.obj *.pdb *.ilk *~ 2>NUL

distclean : clean
	-rm *.exe 2>NUL

testinst : all
	cp *.exe ../../../nbbuild/netbeans/bin/

test : testinst
	../../../nbbuild/netbeans/bin/nbbluej.exe

test2 : testinst
	../../../nbbuild/netbeans/bin/nbbluej_w.exe
