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
import com.netbeans.ide.filesystems.FileSystem;

import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
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
      copyTemplates ();
      restored ();
      if (DEBUG)
	 System.err.println ("CORBA Support Module installed :)");
   }

   public void copyImpls () {

      String[] list_of_files = {"orbacus.impl", "javaorb.impl", "visibroker.impl", "orbixweb.impl",
				"jacorb.impl", "jdk1.2-orb.impl", "orbacus-for-windows.impl"};
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
	       FileObject tmp_file = fo.createData (getFileName (list_of_files[i]), 
						    getFileExt (list_of_files[i]));
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


   public String[] getFileNameAndExt (String name) {
      int index = name.lastIndexOf (".", 0);
      String[] retval = new String[2];
      retval[0] = new String (name.substring (0, index));
      retval[1] = new String (name.substring (index+1, name.length ()));
      if (DEBUG) {
	 System.out.println ("name: " + retval[0]);
	 System.out.println ("ext: " + retval[1]);
      }
      return retval;
   }

   public String getFileName (String name) {
      if (DEBUG)
	 System.out.println ("orig: " + name);
      String retval = name.substring (0, name.lastIndexOf ('.'));
      if (DEBUG)
	 System.out.println ("name: " + retval);
      return retval;
   }

   public String getFileExt (String name) {
      if (DEBUG)
         System.out.println ("orig: " + name);
      String retval = name.substring (name.lastIndexOf ('.')+1, name.length ());
      if (DEBUG)
	 System.out.println ("ext: " + retval);
      return retval;
   }

   
   public void copyTemplates () {

      String[] list_of_templates     = {"Empty.idl", "Simple.idl", "SimpleInterface.idl", 
					"ClientMain.java", "ServerMain.java"};
      String _package =   "/com/netbeans/enterprise/modules/corba/templates";
      TopManager tm = TopManager.getDefault ();
      
      FileObject templates = null;

      templates = tm.getRepository ().getDefaultFileSystem ().getRoot ().getFileObject 
	    ("Templates"); 
      if (templates == null)
	 System.err.println ("can't find system/Templates folder!");

      FileObject fo = null;
	 /*
	 boolean is_corba = false;
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
	 */

      boolean is_corba = false;
      boolean error = false;
	
      try {
	 if (templates != null) {
	    fo = templates.createFolder ("CORBA");
	    is_corba = true;
	 }
      } catch (IOException e) {
	 error = true;
      }
   
      if (error) {
	 fo = templates.getFileObject ("CORBA");
	 if (fo != null)
	    is_corba = true;
	 else {
	    System.err.println ("can't create folder system/Templates/CORBA !");
	    return;
	 }
      }
      try {
	 if (fo.getChildren ().length == 0) {
	    // copy of Templates
	    for (int i=0; i<list_of_templates.length; i++) {
	       FileObject tmp_file = fo.createData (getFileName (list_of_templates[i]),
						    getFileExt (list_of_templates[i]));
	       DataObject.find (tmp_file).setTemplate (true);
	       FileLock lock = tmp_file.lock ();
	       OutputStream o = tmp_file.getOutputStream (lock);
	       if (DEBUG)
		  System.out.println ("file: " + tmp_file );
	       PrintStream out = new PrintStream (o);
	       //String name = _package + "." + list_of_files[i];
	       String name = _package + "/" + list_of_templates[i];
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
      } catch (IOException e) {
	 System.err.println ("unexpected error!");
      }
	    
   }

   /** Module installed again. */
   public void restored() {
      if (DEBUG)
	 System.out.println ("CORBA Support Module restoring...");
      //System.out.println ("setting template map :))");

      Compiler.Manager.register (IDLDataObject.class, new Compiler.Manager () {
	 public void prepareJob (CompilerJob job, Class type, DataObject ido) {
	    ((IDLDataObject)ido).createCompiler (job, type);
	 }
      });

      /*

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

	    // for orbs which has idl compiler implemented in java - for correct works
	    // with classpathsettings from NbProcessDescriptor

	    if (css.getIdl ().getProcessArgs ()[0].equalsIgnoreCase ("java")) {
	       command = css.getIdl ().getProcessArgs ()[0];
	       command = command + " " + css.getIdl ().getClasspathSwitch ();
	       command = command + " " + getClasspath (css.getIdl ().getClasspathItems ());
	       command = command + " ";
	       for (int i=0; i<css.getIdl ().getProcessArgs().length-1; i++)
		  command = command + css.getIdl ().getProcessArgs()[i+1];
	       command = command + " ";
	    }
	    else {
	       command = command + css.getIdl ().getProcessName () + " ";
	    }
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


      */

	    /*
	    if (DEBUG)
	       System.out.println ("sub: " + css.getIdl ().getProcessArgs ()[0]);
	    if (css.getIdl ().getProcessArgs ()[0].equalsIgnoreCase ("java")) {
	       if (DEBUG)
		  System.out.println ("java");
	       if (DEBUG) {
		  System.out.println ("process: " + css.getIdl ().getProcessName ());
		  int length = css.getIdl ().getProcessArgs ().length;
		  String[] params = css.getIdl ().getProcessArgs ();
		  for (int i=0; i<length; i++)
		     System.out.println ("param[" + i + "]: " + params[i]);
	       }
	       new ExternalCompiler(job, ido.getPrimaryFile(), type, css.getIdl (), eexpr);
	    }
	    else
	    */

      /*
	       new ExternalCompiler(job, ido.getPrimaryFile(), type, desc, eexpr);
	 }
      */
      
      //JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
      
      if (DEBUG)
	 System.err.println ("CORBA Support Module restored...");
      
   }
   
   private String getClasspath(String[] classpathItems) {

      /*

    StringBuffer buff = new StringBuffer(100);
    for (int i = 0; i < classpathItems.length; i++) {
       if (NbProcessDescriptor.CP_REPOSITORY.equals (classpathItems[i])) {
        Enumeration ee = com.netbeans.ide.TopManager.getDefault().getRepository ().getFileSystems();
        FileSystem fs;
	String path;
        while (ee.hasMoreElements()) {
          path = "";
          //try {
            fs = (FileSystem) ee.nextElement();
	    //            if (!fs.getUseInCompiler())
	    //              continue;
            //fs.prepareEnvironment(this);
	    //} catch (EnvironmentNotSupportedException ex) {
            //continue;
	    //}
	    //System.out.println ("fs: " + fs.toString ());
	    //System.out.println ("fs2:" + fs.getSystemName ());
	    path = fs.getSystemName ();
	    buff.append(path);
	    buff.append(java.io.File.pathSeparatorChar);
        }
       } else if (NbProcessDescriptor.CP_SYSTEM.equals (classpathItems[i])) {
        buff.append(getSystemEntries());
        buff.append(java.io.File.pathSeparatorChar);
       } else {
        buff.append (classpathItems[i]);
        buff.append(java.io.File.pathSeparatorChar);
       }
    }

    return buff.toString ();

      */
      return null;

   }

   private static final String getSystemEntries() {

      /*

      // boot
    String boot = System.getProperty("sun.boot.class.path");
    StringBuffer sb = (boot != null ? new StringBuffer(boot) : new StringBuffer());
    if (boot != null) {
      sb.append(java.io.File.pathSeparatorChar);
    }

    // modules & libs
    final String[] libs = TopManager.getDefault().getCompilationEngine().getLibraries();
    for (int i = 0; i < libs.length; i++) {
      sb.append(libs[i]);
      sb.append(java.io.File.pathSeparatorChar);
    }

    // classpath
    sb.append(System.getProperty("java.class.path"));

    // std extensions
    String extensions = System.getProperty("java.ext.dirs");
    if (extensions != null) {
      for (StringTokenizer st = new StringTokenizer(extensions, File.pathSeparator); 
	   st.hasMoreTokens ();) {
        String dir = st.nextToken();
        File file = new File(dir);
        if (!dir.endsWith(File.separator)) dir += File.separator;
        if (file.isDirectory()) {
          String[] files = file.list();
          for (int i = 0; i < files.length; i++) {
            String entry = files[i];
            if (entry.endsWith(".jar"))
              sb.append(java.io.File.pathSeparatorChar).append(dir).append(entry);
          }
        }
      }
    }
    return sb.toString();

      */
      return null;
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
 *  9    Gandalf   1.8         6/4/99   Karel Gardas    
 *  8    Gandalf   1.7         5/28/99  Karel Gardas    
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */



