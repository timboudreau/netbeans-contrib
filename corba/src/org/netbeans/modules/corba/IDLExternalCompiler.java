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

import java.io.IOException;

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.compiler.ExternalCompiler;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileStateInvalidException;

/** Object that represents one file containing image in the tree of
* beans representing data systems.
*
* @author Karel Gardas
*/
public class IDLExternalCompiler extends ExternalCompiler {

  /** copy of type */
  protected final Object type;

  public IDLExternalCompiler(Compiler[] dependencies, FileObject fo, Object type, 
			     NbProcessDescriptor nbDescriptor, 
			     ExternalCompiler.ErrorExpression err) {
     super(dependencies, fo, type, nbDescriptor, err);
     this.type = type;
  }

   public IDLExternalCompiler(CompilerJob job, FileObject fo, Object type, 
			      NbProcessDescriptor nbDescriptor, 
			      ExternalCompiler.ErrorExpression err) {
      super(job, fo, type, nbDescriptor, err);
      this.type = type;
   }

  /**
   */
   public Class compilerGroupClass() {
      return IDLExternalCompilerGroup.class;
   }

   /*
   public String getFileName() {
      return getFileObject().getPackageName('.');
   }
   */

  /** Find brother of the file object with NAME+suffix and class extension
   *
   */

   /*
  private static FileObject findBrotherClass(FileObject fo, String suffix) {
     return null;
  }
   */

  /** Checks if stub and skeleton are up to date.
   *
   */

   /*
  public boolean isUpToDate() {
    if (type == BUILD) return false;
    if (type == CLEAN) {
      // delete skeleton and stub
      cleanStub(IDLDataLoader.STUB_SUFFIX);
      cleanStub(IDLDataLoader.SKEL_SUFFIX);
      return false;
    }
    // check  skeleton and stub
    return isUpToDate(IDLDataLoader.STUB_SUFFIX) && isUpToDate(IDLDataLoader.SKEL_SUFFIX);
  }
   */

  /** Is up to date file 
   *
   */

   /*
  private boolean isUpToDate(String suffix) {
    FileObject masterfo = getFileObject(), fo = findBrotherClass(masterfo, suffix);
    if (fo == null || fo.lastModified ().compareTo (masterfo.lastModified ()) < 0) {
      return false;
    }
    return true;
  }
   */

  /** Clean given file object.
   *
   */

   /*
  private void cleanStub(String suffix) {
    FileObject masterfo = getFileObject(), fo = findBrotherClass(masterfo, suffix);
    FileLock lock = null;
    
    if (fo == null) {
      return;
    } else {
      try {
	lock = fo.lock();
	fo.delete(lock);
      } catch (IOException e) {
      } finally {
	if (lock != null) {
	  lock.releaseLock();
	}
      }
    }
  }
   */


  /** Identifier for type of compiler. This method allows subclasses to specify 
   * the type this compiler belongs to. Compilers that belong to the same class
   * will be compiled together by one external process.
   * <P>
   * It is necessary for all compilers of the same type to have same process
   * descriptor and error expression.
   * <P>
   * This implementation returns the process descriptor, so all compilers
   * with the same descriptor will be compiled at once.
   *
   * @return key to define type of the compiler (file object representing root of filesystem) 
   *         or null if there are any errors
   * @see ExternalCompilerGroup#createProcess
   */

  protected Object compilerType () {
     //   try {
       System.err.println("IDLExternalCompiler: compiler type = " 
			  + getFileObject());
       return getFileObject ();
       // } catch (FileStateInvalidException ex) {
       //return new Object ();
       //}
  }

}
