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

//package com.netbeans.enterprise.modules.corba;
package com.netbeans.enterprise.modules.corba;
import org.openide.loaders.DataObject;
import org.openide.compiler.CompilerType;
import org.openide.compiler.CompilerJob;

/** 
*
* @author Karel Gardas
*/           
public class IDLCompilerType extends CompilerType {

  //public static final boolean DEBUG = true;
  private static final boolean DEBUG = false;

  /** Prepare a data object for compilation.
  * Implementations should create an instance of a
  * suitable subclass of {@link Compiler}, passing
  * the compiler job to the constructor so that the job may
  * register the compiler.
  *
  * @param job compiler job to add compilers to
  * @param type the type of compilation task to manage
  * ({@link org.openide.cookies.CompilationCookie.Compile}, etc.)
  * @param obj data object to prepare for compilation
  */

  public void prepareJob (CompilerJob job, Class type, DataObject obj) {
    if (DEBUG)
      System.out.println ("IDLCompilerType::prepareJob (...)");
    if (obj instanceof IDLDataObject)
      ((IDLDataObject)obj).createCompiler(job, type);
  }
}

/*
* <<Log>>
*  4    Gandalf   1.3         11/4/99  Karel Gardas    update from CVS
*  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems copyright in file comment
*  2    Gandalf   1.1         10/5/99  Karel Gardas    
*  1    Gandalf   1.0         10/5/99  Karel Gardas    initial revision
* $
*/
