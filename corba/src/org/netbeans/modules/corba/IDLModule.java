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

import com.netbeans.ide.TopManager;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.filesystems.FileLock;

import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
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
      copyImpls ();
      restored ();
      if (DEBUG)
	 System.err.println ("CORBA Support Module installed :)");
   }

   public void copyImpls () {

      String[] list_of_files = {"orbacus", "javaorb", "visibroker", "orbixweb", "jacorb"};
      String _package =   "/com/netbeans/enterprise/modules/corba/impl";
      TopManager tm = TopManager.getDefault ();
      
      try {
	 Enumeration folders = tm.getRepository ().getDefaultFileSystem ().getRoot ().getFolders 
	    (false);
	 boolean is_corba = false;
	 FileObject fo = null;
	 for (int i=1; folders.hasMoreElements (); ) {
	    fo = (FileObject)folders.nextElement ();
	    if (fo.toString ().equals ("CORBA")) {
	       // it exists 
	       if (DEBUG)
		  System.out.println ("CORBA exists :-)");
	       is_corba = true;
	       break;
	    }
	 }
	 if (!is_corba) {

	    FileObject system = tm.getRepository ().getDefaultFileSystem ().getRoot ();
	    fo = system.createFolder ("CORBA");
	 }

	 if (fo.getChildren ().length == 0) {
	    // copy of implementations files
	    for (int i=0; i<list_of_files.length; i++) {
	       FileObject tmp_file = fo.createData (list_of_files[i], "impl");
	       FileLock lock = tmp_file.lock ();
	       OutputStream o = tmp_file.getOutputStream (lock);
	       if (DEBUG)
		  System.out.println ("file: " + tmp_file );
	       PrintStream out = new PrintStream (o);
	       //String name = _package + "." + list_of_files[i];
	       String name = _package + "/" + list_of_files[i];
	       if (DEBUG)
		  System.out.println ("name: " + name);
	       InputStream input = CORBASupportSettings.class.getResourceAsStream 
		  (name);
	       
	       if (input == null) {
		  System.err.println ("can't find " + name + " resource.");
		  continue;
	       }
	       BufferedReader in = new BufferedReader (new InputStreamReader (input));
	       String tmp;
	       try {
		  while ((tmp = in.readLine ()) != null) {
		     out.println (tmp);
		  }
	       } catch (IOException e) {
		  e.printStackTrace ();
	       }
	       
	    }
	 }
	 else {
	    if (DEBUG)
	       System.out.println ("in system/CORBA exists files :-)");
	 }
      } catch (IOException e) {
	 e.printStackTrace ();
      }
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
	    CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
	       (CORBASupportSettings.class, true);	   
	    ExternalCompiler.ErrorExpression eexpr = new ExternalCompiler.ErrorExpression 
	       ("blabla", css.getErrorExpression (), css.file (), 
		css.line (), css.column (), css.message ());

	    //String expression = "^IDL Compiler: ([^ ]+)\n^([0-9]+):(.*): (.*)";
	    //System.out.println ("manual expr: " + expression);
	    //ExternalCompiler.ErrorExpression eexpr = new ExternalCompiler.ErrorExpression 
	    //  ("blabla", expression , CORBASupportSettings.file (), 
	    //   CORBASupportSettings.line (), CORBASupportSettings.column (), 
	    //   CORBASupportSettings.message ());
	    
	    //if (DEBUG)
	    //   System.out.println ("expression: " + css.expression () + ", " + css.file () + ", " 
	    //			   + css.line () + ", " + css.column () + ", " + css.message ());
	    
	    //String[] tmps1 = new String[1];
	    //tmps1[0] = new String ("");
	    //tmps2[0] = new String ("");
	    String command = new String ();
	    command = command + css.getIdl () + " ";
	    if (CORBASupportSettings.param () != null)
	       if (!CORBASupportSettings.param ().equals ("")) {
		  command = command + CORBASupportSettings.param () + " ";
	       }
	    if (css.isTie ()) {
	       command = command + css.getTieParam () + " ";
	    }
	    command = command + css.getPackageParam ();
	    command = command + ido.getPrimaryFile ().getParent ().getPackageName 
	       (css.delim ()) + " ";
	    //command = command + ido.getPrimaryFile ().getParent ().getPackageName 
	    //    ('/') + " ";
	    command = command + css.getDirParam ();
	    
	    String file = "";
	    try {
	       file =  ido.getPrimaryFile ().getFileSystem ().getSystemName();
	       command = command + file;
	    } catch (com.netbeans.ide.filesystems.FileStateInvalidException ex) {
	       System.out.println (ex);
	    }
	    
	    
	    if (DEBUG) {
	       file = file + "/" + ido.getPrimaryFile ();
	       System.out.println ("command: " + command);
	       System.out.println ("file: " + file);
	       System.out.println ("prim: " + ido.getPrimaryFile ());
	    }
	    String[] tmps2 = new String[] {NbProcessDescriptor.CP_REPOSITORY};
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
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */



