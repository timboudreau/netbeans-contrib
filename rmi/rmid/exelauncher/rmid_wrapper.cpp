/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/* $Id$ */

#include "stdafx.h"

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <io.h>
#include <fcntl.h>
#include <process.h>
#include <commdlg.h>
#include <signal.h>

#define RMID_MAIN_CLASS "org.netbeans.rmi.RMIDWrapperImpl"
#define JDK_KEY "Software\\JavaSoft\\Java Development Kit"
//#define IDE_KEY "Software\\netbeans.org\\NetBeans IDE"
#define IDE_KEY "Software\\Sun Microsystems, Inc.\\Forte for Java"
#define ESCAPE '\\'
#define DQUOTE '\"'
#define QUOTE  '\''
#define SPACE  ' '

// #define DEBUG

static char jdkhome[MAX_PATH];
static char idehome[MAX_PATH];
static char classpath[1024 * 16];

static void addToClassPath(const char *pathprefix, const char *path);
static int findJdkFromRegistry(const char* keyname, char jdkhome[]);
static void setIDEHome(char idehome[]);
static int processOptions(int argc, char **argv, char ***cmd);
static void UserHandler(int sig);
static unsigned int getLength (char* str);
static char* createCmd (char* cmd, char** params);


static void sysSignal(int sig, void (*func) ( int sig )) {
    signal(sig, (void (*)(int)) func);
}
 

/**
 * CTRL+Break handler
 */
static void UserHandler(int sig)
{
    // ignore it
    /* We need to reinstate the signal handler each time... */
    sysSignal (sig, UserHandler);
}

int main(int argc, char *argv[]) {
    long status;
    char buf[1024];
    char **cmd;
    STARTUPINFO start;
    PROCESS_INFORMATION child;
	ZeroMemory( &start, sizeof(start) );
    start.cb = sizeof(start);
    ZeroMemory( &child, sizeof(child) );

    sysSignal(SIGBREAK, UserHandler);

    setIDEHome(idehome);
    findJdkFromRegistry(JDK_KEY, jdkhome);
    
    // printf("%s, %s\n", idehome, jdkhome);
    processOptions(argc, argv, &cmd);
    strcpy(buf, jdkhome);
    strcat(buf, "\\bin\\java.exe");

#ifdef DEBUG
    fprintf(stderr, "process: %s\n", buf);
    fprintf(stderr, "arguments: ");
    for(int i = 0; cmd[i] != NULL; i++) {
        if (i != 0) fprintf(stderr, ", ");
        fprintf(stderr, "%s", cmd[i]);
    }
    printf("\n");
#endif DEBUG
    char* cmdLine = createCmd (buf, cmd);
#ifdef DEBUG
    fprintf (stderr, "commandLine: %s", cmdLine);
#endif // DEBUG    
    status = CreateProcess (NULL,cmdLine,NULL, NULL, FALSE, NORMAL_PRIORITY_CLASS, NULL, NULL, &start, &child);
	WaitForSingleObject (child.hProcess, INFINITE);
//    execv(buf, cmd);
    if (status == -1) {
        perror(buf);
    }
    free ((void*) cmdLine);
    return status;
}

/*
 * Returns string data for the specified registry value name, or
 * NULL if not found.
 */
static char * GetStringValue(HKEY key, const char *name)
{
    DWORD type, size;
    char *value = 0;

    if (RegQueryValueEx(key, name, 0, &type, 0, &size) == 0 && type == REG_SZ) {
        value = (char*) malloc(size);
        if (RegQueryValueEx(key, name, 0, 0, (unsigned char*)value, &size) != 0) {
            free(value);
            value = 0;
        }
    }
    return value;
}

static int findJdkFromRegistry(const char* keyname, char jdkhome[])
{
    HKEY hkey = NULL, subkey = NULL;
    char *ver = NULL;
    int rc = 1;
  
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, keyname, 0, KEY_READ, &hkey) == 0) {
        ver = GetStringValue(hkey, "CurrentVersion");
        if (ver == NULL)
            goto quit;

        if (RegOpenKeyEx(hkey, ver, 0, KEY_READ, &subkey) == 0) {
            char *home = GetStringValue(subkey, "JavaHome");
            if (home == NULL)
                goto quit;
            strcpy(jdkhome, home);
            free(home);
            rc = 0;
        }
    }

  quit:
    if (ver != NULL)
        free(ver);
    if (subkey != NULL)
        RegCloseKey(subkey);
    if (hkey != NULL)
        RegCloseKey(hkey);
    return rc;
}

void addToClassPath(const char *pathprefix, const char *path) {
    char buf[1024];
    
    strcpy(buf, pathprefix);
    if (path != NULL)
        strcat(strcat(buf, "\\"), path);

    if (classpath[0] != '\0')
        strcat(classpath, ";");
    strcat(classpath, buf);
}

/** Set IDE home. Get the whole process name and strip
 * the trailig \bin\<name>
 */
static void setIDEHome(char idehome[]) {
    char exepath[1024 * 4];
    char buf[1024 * 4], *pc;

    GetModuleFileName(0, buf, sizeof buf);
    strcpy(exepath, buf);

    pc = strrchr(buf, '\\');
    if (pc != NULL) {             // always holds
        *pc = '\0';	// remove .exe filename
    }

    // remove \bin
    pc = strrchr(buf, '\\');
    if (pc != NULL && 0 == stricmp("\\bin", pc)) {
        *pc = '\0';
    }
    strcpy(idehome, buf);
}


/** Process arguments.

-J options will be passed to java
the rest will remain and will processed by rmid

some exceptions

-Xms8m - 
-jdkhome  - obtain programatically and delete from list of options
-J-Djava.security.policy= - rmid.policy
// -log - ../tmp/
// -J-Dsun.rmi.server.activation.debugExec - default to true
-J-classpath - add library to the classpath


 */
static int processOptions(int argc, char **argv, char ***cmd) {
    int jac = 0, rac = 0, i, j;
    char *jav[64], *rav[64], *arg, **cmd2;
    int cpi = -1, spi = -1, nbi = -1, jdkhomeFlag = 0, cpFlag = 0;
    char buf[1024];
    
    jav[jac++] = "-ms8m";
    jav[jac++] = "-Dsun.rmi.server.activation.debugExec=true";
    
    for(i = 1; i < argc; i++) {
        arg = argv[i];
        if ((arg[0] == '-') && (arg[1] == 'J')) {
            arg += 2;
            jav[jac] = arg;
            
            if (cpFlag) {
                cpFlag = 0;
            }    
            
            if (strncmp(arg, "-Djava.security.policy=", 23) == 0) {
                spi = jac;
            }
            
            if (strncmp(arg, "-Dnetbeans.home=", 16) == 0) {
                nbi = jac;
            }

            if (strcmp(arg, "-classpath") == 0) {
                cpi = jac;
                cpFlag = 1;
            }
            
            jac++;
        } else {
            
            if (jdkhomeFlag) {
                strcpy(jdkhome, arg);
                jdkhomeFlag = 0;
                continue;
            }
            
            if (strcmp(arg, "-jdkhome") == 0) {
                jdkhomeFlag = 1;
            } else {
                rav[rac++] = arg;
            }
        }
    }

    if (jdkhomeFlag) {
        jdkhome[0] = 0;
    }

    // add policy file    
    if (spi == -1) {
        jav[jac++] = "-Djava.security.policy=rmid.policy";
    } 
    
    if (nbi == -1) {
        strcpy(buf, "-Dnetbeans.home=");
        strcat(buf, idehome);
        jav[jac++] = strdup(buf);
    } else {
        char *p = strchr(jav[nbi], '=');
        if (p != NULL) {
            strcpy(idehome, p + 1);            
        }
    }
    
    // update classpath
    if (cpi == -1) {
        jav[jac++] = "-classpath";
        strcpy(buf, idehome);
        strcat(buf, "\\lib\\ext\\rmi-ext.jar");        
        jav[jac++] = strdup(buf);
    } else if (cpFlag) {
        strcpy(buf, idehome);
        strcat(buf, "\\lib\\ext\\rmi-ext.jar");        
        jav[jac++] = strdup(buf);
    } else {
        strcpy(buf, jav[cpi + 1]);
        strcat(buf, ";");
        strcat(buf, idehome);
        strcat(buf, "\\lib\\ext\\rmi-ext.jar");        
        jav[cpi + 1] = strdup(buf);                
    }
    
    // add ide.home

    argc = jac + rac + 3; // process name, class, NULL
    cmd2 = *cmd = (char **) malloc(argc * sizeof(char *));
    
    cmd2[0] = argv[0];
    j = 1;
    for(i = 0; i < jac; i++) {
        cmd2[j++] = jav[i];
    }
    cmd2[j++] = RMID_MAIN_CLASS;
    for(i = 0; i < rac; i++) {
        cmd2[j++] = rav[i];
    }
    cmd2[j] = NULL;
         
    // test
    // for(i = 0; i < argc; i++) printf("%s\n", cmd2[i]);

    return 0;    
}

static char* createCmd (char* cmd, char** params) {
  int length = 1;	// Ending NULL
  int i,j,k;
  char* arg;
  length += (getLength (cmd)-1);
  for (i=0; params[i]; i++)
	  length += getLength (params[i]);
  char* cmdLine = (char*) malloc (length);
  k=0;
  cmdLine[k++] = DQUOTE;
  for (j=0; cmd[j]; j++) {
	cmdLine[k++] = cmd[j];
  }
  cmdLine[k++] = DQUOTE;
  for (i=1; params[i]; i++) {
	  arg = params[i];
	  cmdLine[k++] = SPACE;
	  cmdLine[k++] = DQUOTE;
	  for (j=0; arg[j]; j++) {
		  cmdLine[k++] = arg[j];
	  }
	  cmdLine[k++] = DQUOTE;
  }
  cmdLine[k] = 0x0;
  return cmdLine;
}

static unsigned int getLength (char* str) {
  unsigned int length = 3 + strlen (str);	// twice " and SPACE
  return length;
}


