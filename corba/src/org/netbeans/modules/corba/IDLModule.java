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

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.compiler.ExternalCompiler;
import org.openide.modules.ModuleInstall;
import org.openide.loaders.DataObject;

import com.netbeans.developer.modules.loaders.java.settings.JavaSettings;
import com.netbeans.developer.modules.loaders.java.settings.ExternalCompilerSettings;
import org.openide.execution.NbProcessDescriptor;

import org.openide.TopManager;
import org.openide.filesystems.FileSystem;

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
        if (DEBUG) System.err.println ("CORBA Support Module installing...");
      copyImpls ();
      copyTemplates ();

      restored ();
        if (DEBUG) System.err.println ("CORBA Support Module installed :)");
   }


   /** Module installed again. */
   public void restored() {
        if (DEBUG) System.out.println ("CORBA Support Module restoring...");
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
	    } catch (org.openide.filesystems.FileStateInvalidException ex) {
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
        Enumeration ee = org.openide.TopManager.getDefault().getRepository ().getFileSystems();
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
   
// -----------------------------------------------------------------------------
// Private methods
  
  private void copyTemplates () {
    try {
      org.openide.filesystems.FileUtil.extractJar (
        org.openide.TopManager.getDefault ().getPlaces ().folders().templates ().getPrimaryFile (),
        getClass ().getClassLoader ().getResourceAsStream ("com/netbeans/enterprise/modules/corba/resources/templates.jar")
      );
    } catch (java.io.IOException e) {
      org.openide.TopManager.getDefault ().notifyException (e);
    }
  }

  private void copyImpls () {
    try {
      org.openide.filesystems.FileUtil.extractJar (
        org.openide.TopManager.getDefault ().getRepository ().getDefaultFileSystem ().getRoot (),
        getClass ().getClassLoader ().getResourceAsStream ("com/netbeans/enterprise/modules/corba/resources/impls.jar")
      );
    } catch (java.io.IOException e) {
      org.openide.TopManager.getDefault ().notifyException (e);
    }
  }
}

/*
 * <<Log>>
 *  13   Gandalf   1.12        6/10/99  Ian Formanek    Modified copying 
 *       templates and impls on install
 *  12   Gandalf   1.11        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  11   Gandalf   1.10        6/4/99   Karel Gardas    
 *  10   Gandalf   1.9         6/4/99   Karel Gardas    
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



