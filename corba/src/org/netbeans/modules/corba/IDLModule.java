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

package com.netbeans.enterprise.modules.corba;

import com.netbeans.ide.compiler.Compiler;
import com.netbeans.ide.compiler.CompilerJob;
import com.netbeans.ide.compiler.ExternalCompiler;
import com.netbeans.ide.modules.ModuleInstall;
import com.netbeans.ide.loaders.DataObject;

import com.netbeans.developer.modules.loaders.java.settings.JavaSettings;
import com.netbeans.developer.modules.loaders.java.settings.ExternalCompilerSettings;
import com.netbeans.ide.execution.NbProcessDescriptor;


import java.util.Properties;
import java.io.*;

import com.netbeans.enterprise.modules.corba.settings.*;

/**
* Module installation class for IDLDataObject.
*
* @author Karel Gardas
*/
public class IDLModule implements ModuleInstall {

   private static final boolean DEBUG = false;
   //private static final boolean DEBUG = true;
   
   /** Module installed for the first time. */
   public void installed() {
      if (DEBUG)
	 System.err.println ("CORBA Support Module installing...");
      restored ();
      if (DEBUG)
	 System.err.println ("CORBA Support Module installed :)");
   }

   /** Module installed again. */
   public void restored() {
      if (DEBUG)
	 System.out.println ("CORBA Support Module restoring...");
      //System.out.println ("setting template map :))");
      
      Compiler.Manager.register (IDLDataObject.class,
				 new Compiler.Manager() {
	 public void prepareJob(CompilerJob job, Class type, DataObject ido) {
	    if (DEBUG)
	       System.out.println ("prepareJob...");
	    ExternalCompiler.ErrorExpression eexpr = new ExternalCompiler.ErrorExpression 
	       ("blabla", CORBASupportSettings.expression (), CORBASupportSettings.file (), 
		CORBASupportSettings.line (), CORBASupportSettings.column (), 
		CORBASupportSettings.message ());
	    //String expression = "^IDL Compiler: ([^ ]+)\n^([0-9]+):(.*): (.*)";
	    //System.out.println ("manual expr: " + expression);
	    //ExternalCompiler.ErrorExpression eexpr = new ExternalCompiler.ErrorExpression 
	    //  ("blabla", expression , CORBASupportSettings.file (), 
	    //   CORBASupportSettings.line (), CORBASupportSettings.column (), 
	    //   CORBASupportSettings.message ());

	    if (DEBUG)
	       System.out.println ("expression: " + CORBASupportSettings.expression () + ", " + CORBASupportSettings.file () + ", " + CORBASupportSettings.line () + ", " + CORBASupportSettings.column () + ", " + CORBASupportSettings.message ());

	    String[] tmps1 = new String[1];
	    tmps1[0] = new String ("");
	    String[] tmps2 = new String[] {NbProcessDescriptor.CP_REPOSITORY};
	    //tmps2[0] = new String ("");
	    String command = new String ();
	    command = command + CORBASupportSettings.idl () + " ";
	    if (CORBASupportSettings.param () != null)
	       if (!CORBASupportSettings.param ().equals ("")) {
		  command = command + CORBASupportSettings.param () + " ";
	       }

	    command = command + CORBASupportSettings.package_param ();
	    command = command + ido.getPrimaryFile ().getParent ().getPackageName 
	       (CORBASupportSettings.delim ()) + " ";
	    //command = command + ido.getPrimaryFile ().getParent ().getPackageName 
	    //    ('/') + " ";
	    command = command + CORBASupportSettings.dir_param ();

	    String file = "";
	    try {
	       file =  ido.getPrimaryFile ().getFileSystem ().getSystemName();
	       command = command + file;
	    } catch (com.netbeans.ide.filesystems.FileStateInvalidException ex) {
	       System.out.println (ex);
	    }

	    file = file + "/" + ido.getPrimaryFile ();

	    if (DEBUG) {
	       System.out.println ("command: " + command);
	       System.out.println ("file: " + file);
	       System.out.println ("prim: " + ido.getPrimaryFile ());
	    }
	    NbProcessDescriptor desc = new NbProcessDescriptor 
	       (command , NbProcessDescriptor.NO_SWITCH, tmps2);
	    new ExternalCompiler(job, ido.getPrimaryFile(), type, desc, eexpr);
	 }
      }
				 );

      //JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
      
      if (DEBUG)
	 System.err.println ("CORBA Support Module restored...");
   }

   /** Module was uninstalled. */
   public void uninstalled() {
   }

   /** Module is being closed. */
   public boolean closing () {
      return true; // agree to close
   }

}

/*
 * <<Log>>
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */


