#
# Gererated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/Debug/GNU-Linux-x86

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/headers/simple/subdir_1.o \
	${OBJECTDIR}/main.o \
	${OBJECTDIR}/headers/simple/simple_1.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS} dist/Debug/GNU-Linux-x86/syntaxerr_unit_nursery

dist/Debug/GNU-Linux-x86/syntaxerr_unit_nursery: ${OBJECTFILES}
	${MKDIR} -p dist/Debug/GNU-Linux-x86
	${LINK.cc} -o dist/Debug/GNU-Linux-x86/syntaxerr_unit_nursery ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/headers/simple/subdir_1.o: headers/simple/subdir_1.cpp 
	${MKDIR} -p ${OBJECTDIR}/headers/simple
	$(COMPILE.cc) -g -o ${OBJECTDIR}/headers/simple/subdir_1.o headers/simple/subdir_1.cpp

${OBJECTDIR}/main.o: main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.cc) -g -o ${OBJECTDIR}/main.o main.cpp

${OBJECTDIR}/headers/simple/simple_1.o: headers/simple/simple_1.cpp 
	${MKDIR} -p ${OBJECTDIR}/headers/simple
	$(COMPILE.cc) -g -o ${OBJECTDIR}/headers/simple/simple_1.o headers/simple/simple_1.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Debug
	${RM} dist/Debug/GNU-Linux-x86/syntaxerr_unit_nursery

# Subprojects
.clean-subprojects:
