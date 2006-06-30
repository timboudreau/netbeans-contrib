/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.compiler.jdk13;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Compiler {

    public static final boolean DEBUG = true;
    //public static final boolean DEBUG = false;

    public static void main (String[] args) throws Exception {
	System.err.println ("Try to run with Sun or IBM JDK 1.3 IDL Compiler");
	boolean __cant_find = false;
	try {
	    Class __compiler = Class.forName ("com.sun.tools.corba.se.idl.Compile"); // NOI18N
	    System.err.println ("Run Sun JDK 1.3 IDL Compiler");
	    org.netbeans.modules.corba.idl.compiler.jdk13sun.Compiler.main (args);
	    System.exit (0);
	} catch (ClassNotFoundException __ex) {
	    __cant_find = true;
	} catch (Exception __ex) {
	    throw __ex;
	}
	if (__cant_find) {
	    try {
		Class __compiler = Class.forName ("com.ibm.idl.Compile"); // NOI18N
		System.err.println ("Run IBM JDK 1.3 IDL Compiler");
		org.netbeans.modules.corba.idl.compiler.jdk13ibm.Compiler.main (args);
		System.exit (0);
	    } catch (ClassNotFoundException __ex) {
	    }
	}
	System.err.println ("Can't find JDK 1.3 IDL compiler.");
    }

}

/*
 * $Log
 * $
 */

