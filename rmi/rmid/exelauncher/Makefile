# -*- Makefile -*-
#
#  SUN PROPRIETARY/CONFIDENTIAL: INTERNAL USE ONLY.
#  Copyright (c) 2000 Sun Microsystems, Inc. All rights reserved.
#  Use is subject to license terms.
#
# $Id$

JDK_HOME = c:\jdk1.3

CFLAGS = -nologo "-I$(JDK_HOME)\include" "-I$(JDK_HOME)\include\win32"

!ifdef DEBUG
CFLAGS = $(CFLAGS) -Zi
!endif

LIBS = advapi32.lib user32.lib
PROG = rmid_wrapper

all : $(PROG).exe 

$(PROG).exe : rmid_wrapper.cpp
	cl $(CFLAGS) $(LIBS) -Fe$@ $**

$(PROG).res : $(PROG).rc
	rc /fo$@ $**

$(PROG)w.exe : runide.cpp
	cl $(CFLAGS) $(LIBS) -Fe$@ /DWINMAIN $**

clean :
	-del *.res *.obj *.pdb *.ilk *~ 2>NUL

distclean : clean
	-del $(PROG).exe $(PROG)w.exe

insttest : all
	copy $(PROG).exe "c:\netbeans\bin"
#	copy $(PROG)w.exe "c:\netbeans\bin"

inst :
	copy $(PROG).exe ..\release\bin
#	copy $(PROG)w.exe ..\release\bin
