/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.compiler.jdk14;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.SymtabFactory;
import com.sun.tools.corba.se.idl.IncludeEntry;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.ModuleEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionBranch;
import com.sun.tools.corba.se.idl.UnionEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.InvalidArgument;
import com.sun.tools.corba.se.idl.PragmaEntry;

import com.sun.tools.corba.se.idl.toJavaPortable.Compile;
import com.sun.tools.corba.se.idl.toJavaPortable.Util;
//import com.sun.tools.corba.se.idl.toJavaPortable.Factories;
//import com.sun.tools.corba.se.idl.toJavaPortable.Util;
//import com.sun.tools.corba.se.idl.toJavaPortable.Util;

public class Compiler extends com.sun.tools.corba.se.idl.Compile {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;
  
    public static void main (String[] args) {
	Compiler comp = new Compiler ();
	/*
	 *
	 * options --directory  <dir> --package <package> --tie
	 * --directory => -td
	 * --package => -pkgPrefix
	 * --tie => -fallTIE - else -fall
	 * other options
	 *
	 */
	Vector __parser_args = new Vector ();
	for (int i=0; i<args.length; i++) {
	    if (DEBUG)
		System.out.println ("param: " + args[i]); // NOI18N
	    if (!args[i].equals ("--directory")) {
		if (!args[i].equals ("--package")) {
		    if (!args[i].equals ("--tie")) {
			if (DEBUG)
			    System.out.println ("adding parser param: " + args[i]);
			__parser_args.add (args[i]);
		    }
		}
		else {
		    // removing package name
		    i++;
		}
	    }
	    else {
		// removing directory name
		i++;
	    }
	} 
	String file_name = args[args.length - 1];
	if (DEBUG)
	    System.out.println ("idl name: " + file_name); // NOI18N
	String[] parser_args = new String[__parser_args.size ()];
	parser_args = (String[])__parser_args.toArray (parser_args);
	Vector names = new Vector ();
	try {
	    comp.init (parser_args);
	    java.util.Enumeration en = comp.parse ();
	    if (en == null)
		return;
	    while (en.hasMoreElements ()) {
                SymtabEntry _se = (SymtabEntry)en.nextElement (); 
                if (_se instanceof IncludeEntry || _se instanceof PragmaEntry)
                    continue;
		String name = _se.fullName ();
		if (DEBUG)
		    System.out.println ("element: " + name); // NOI18N
		if (name.indexOf ('/') == -1) {
		    // top level element
		    names.addElement (name);
		    if (DEBUG)
			System.out.println ("top level element: " + name); // NOI18N
		}
	    }	
	} catch (Exception e) {
	    e.printStackTrace ();
	}

	Vector new_args = new Vector ();
	boolean ties = false;
	for (int i=0; i<args.length-1; i++) {
	    if (args[i].equals ("--directory")) { // NOI18N
		new_args.addElement ("-td"); // NOI18N
		new_args.addElement (args[++i]);
		continue;
	    }
	    if (args[i].equals ("--package")) { // NOI18N
		i++;
		for (int j=0; j<names.size (); j++) {
		    new_args.addElement ("-pkgPrefix"); // NOI18N
		    new_args.addElement ((String)names.elementAt (j));
		    new_args.addElement (args[i]);
		}
		continue;
	    }
	    if (args[i].equals ("--tie")) { // NOI18N
		new_args.addElement ("-fallTIE"); // NOI18N
		ties = true;
		continue;
	    }
	    // other parameters (JDK1.4 IDL compliant)
	    new_args.addElement (args[i]);
	}
	if (!ties)
	    new_args.addElement ("-fall"); // NOI18N
	new_args.addElement (file_name);
	String[] args2 = (String[])new_args.toArray (new String[] {});
	if (DEBUG) {
	    System.out.println ("---");
	    for (int i=0; i<args2.length; i++) {
		System.out.println ("new param: " + args2[i]); // NOI18N
	    }
	}
	if (DEBUG)
	    System.out.println ("Compile.main (" + args2 + ");");
	Compile.main (args2);
    
    }

}

/*
 * $Log
 * $
 */
