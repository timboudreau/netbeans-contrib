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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

import javax.swing.JEditorPane;

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.modules.ModuleInstall;
import org.openide.loaders.DataObject;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileSystem;
import org.openide.TopManager;

import com.netbeans.developer.modules.loaders.java.settings.JavaSettings;
import com.netbeans.developer.modules.loaders.java.settings.ExternalCompilerSettings;
import com.netbeans.developer.modules.text.options.AllOptions;
import com.netbeans.editor.Settings;

import com.netbeans.enterprise.modules.corba.settings.*;
import com.netbeans.enterprise.modules.corba.idl.editor.settings.IDLOptions;


/**
* Module installation class for IDLDataObject.
*
* @author Karel Gardas
*/
public class IDLModule extends ModuleInstall {

  static final long serialVersionUID =8847247042163099527L;

  private static final boolean DEBUG = false;
  //private static final boolean DEBUG = true;

  /** Module installed for the first time. */
  public void installed() {
    if (DEBUG) 
      System.out.println ("CORBA Support Module installing...");
    copyImpls ();
    copyTemplates ();

    restored ();
    if (DEBUG) 
      System.out.println ("CORBA Support Module installed :)");
  }


  /** Module installed again. */
  public void restored() {
    if (DEBUG) 
      System.out.println ("CORBA Support Module restoring...");
    if (DEBUG) 
      System.out.println ("restoring editor support ...");
  
    CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
      (CORBASupportSettings.class, true);
    css.init ();
    
    installColoring ();
    if (DEBUG)
      System.out.println ("CORBA Support Module restored...");
  }
   
  private void installColoring () {
    if (DEBUG)
      System.out.println ("installColoring()");
    try {
      Class settings = Class.forName
        ("com.netbeans.editor.Settings",
         false, this.getClass().getClassLoader()); // only test for editor module
      
      Class restore = Class.forName
        ("com.netbeans.enterprise.modules.corba.idl.editor.settings.RestoreColoring",
         false, this.getClass().getClassLoader());
      Method restoreMethod = restore.getMethod ("restore", null);
      restoreMethod.invoke (restore.newInstance(), null);

    } catch (ClassNotFoundException e) {
      if (DEBUG)
	e.printStackTrace ();
    } catch (NoSuchMethodException e) {
      if (DEBUG)
	e.printStackTrace ();
    } catch (InvocationTargetException e) {
      if (DEBUG)
	e.printStackTrace ();
    } catch (IllegalAccessException e) {
      if (DEBUG)
	e.printStackTrace ();
    } catch (InstantiationException e) {
      if (DEBUG)
	e.printStackTrace ();
    }
    /*
      } catch (Exception ex) {
      ex.printStackTrace ();
      }
    */
  }


  private String getClasspath(String[] classpathItems) {
    return null;
  }

  private static final String getSystemEntries() {
    return null;
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
 *  27   Jaga      1.25.1.0    3/16/00  Miloslav Metelka removed unused import
 *  26   Gandalf   1.25        2/9/00   Karel Gardas    
 *  25   Gandalf   1.24        2/8/00   Karel Gardas    
 *  24   Gandalf   1.23        11/27/99 Patrik Knakal   
 *  23   Gandalf   1.22        11/9/99  Karel Gardas    - updated for new IDL 
 *       Editor Stuff
 *  22   Gandalf   1.21        11/4/99  Karel Gardas    - update from CVS
 *  21   Gandalf   1.20        11/4/99  Karel Gardas    update from CVS
 *  20   Gandalf   1.19        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  19   Gandalf   1.18        10/5/99  Karel Gardas    
 *  18   Gandalf   1.17        10/1/99  Petr Hrebejk    org.openide.modules.ModuleInstall
 *        changed to class + some methods added
 *  17   Gandalf   1.16        10/1/99  Karel Gardas    updates from CVS
 *  16   Gandalf   1.15        9/13/99  Jaroslav Tulach 
 *  15   Gandalf   1.14        8/7/99   Karel Gardas    changes in code which 
 *       hide generated files
 *  14   Gandalf   1.13        8/3/99   Karel Gardas    
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



