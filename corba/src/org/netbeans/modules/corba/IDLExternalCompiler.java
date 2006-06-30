/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba;

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
    protected FileObject _file_object;

    public IDLExternalCompiler(FileObject fo,
                               Object type,
                               NbProcessDescriptor nbDescriptor,
                               ExternalCompiler.ErrorExpression err) {
        super(fo, type, nbDescriptor, err);
        this.type = type;
        _file_object = fo;
    }

    /*
      public IDLExternalCompiler(FileObject fo,
      Object type, 
      NbProcessDescriptor nbDescriptor, 
      ExternalCompiler.ErrorExpression err) {
      super(fo, type, nbDescriptor, err);
      this.type = type;
      }
    */

    public FileObject getIDLFileObject () {
        return getFileObject ();
    }

    public boolean isUpToDate () {
        return false;
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

    /*
      protected Object compilerType () {
      //   try {
      //System.err.println("IDLExternalCompiler: compiler type = " 
      //		  + getFileObject());
      return getFileObject ();
      // } catch (FileStateInvalidException ex) {
      //return new Object ();
      //}
      }
    */

    public Object compilerGroupKey () {
        //   try {
        //System.err.println("IDLExternalCompiler: compiler type = "
        //		  + getFileObject());
        return getFileObject ();
        // } catch (FileStateInvalidException ex) {
        //return new Object ();
        //}
    }

}
