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

package org.netbeans.modules.corba.idl.compiler.jdk13ibm;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.ibm.idl.GenFileStream;
import com.ibm.idl.SymtabFactory;
import com.ibm.idl.IncludeEntry;
import com.ibm.idl.InterfaceEntry;
import com.ibm.idl.InterfaceState;
import com.ibm.idl.ModuleEntry;
import com.ibm.idl.PrimitiveEntry;
import com.ibm.idl.SequenceEntry;
import com.ibm.idl.StructEntry;
import com.ibm.idl.SymtabEntry;
import com.ibm.idl.TypedefEntry;
import com.ibm.idl.UnionBranch;
import com.ibm.idl.UnionEntry;
import com.ibm.idl.ValueEntry;
import com.ibm.idl.ValueBoxEntry;
import com.ibm.idl.InvalidArgument;
import com.ibm.idl.PragmaEntry;

import com.ibm.idl.toJavaPortable.Compile;
import com.ibm.idl.toJavaPortable.Util;
//import com.ibm.idl.toJavaPortable.Factories;
//import com.ibm.idl.toJavaPortable.Util;
//import com.ibm.idl.toJavaPortable.Util;

public class Compiler extends com.ibm.idl.Compile {

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
	    if (!args[i].equals ("--directory"))
		if (!args[i].equals ("--package"))
		    if (!args[i].equals ("--tie"))
			__parser_args.add (args[i]);
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
	    // other parameters (JDK1.3 IDL compliant)
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
