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
