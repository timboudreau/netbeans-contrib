#
#                          Sun Public License Notice
#
# The contents of this file are subject to the Sun Public License Version
# 1.0 (the "License"). You may not use this file except in compliance with
# the License. A copy of the License is available at http://www.sun.com/
#
# The Original Code is the DocSup module.
# The Initial Developer of the Original Code is Jan Lahoda.
# Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
# All Rights Reserved.
#
# Contributor(s): Jan Lahoda.
#
CC=gcc
CCPP=g++
LD=gcc
OPTGEN=gengetopt

all: client

client.o: client.cpp
	$(CCPP) -o client.o -c client.cpp

client: client.o
	$(LD) -o client client.o -lstdc++

