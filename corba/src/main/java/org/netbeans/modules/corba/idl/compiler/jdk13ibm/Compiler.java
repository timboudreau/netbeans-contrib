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
