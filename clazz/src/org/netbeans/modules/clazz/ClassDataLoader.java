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

package org.netbeans.modules.clazz;

import java.io.IOException;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.actions.*;


/** The DataLoader for ClassDataObjects.
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jan Jancura, Ian Formanek, Dafe Simonek
*/
public final class ClassDataLoader extends MultiFileLoader {

    /** Extension constants */
    private static final String SER_EXT = "ser"; // NOI18N
    private static final String CLASS_EXT = "class"; // NOI18N
    private static final String REPRESENTATION_CLASS_NAME = 
        "org.netbeans.modules.clazz.ClassDataObject"; // NOI18N

    private static final char INNER_CLASS_DIVIDER = '$';

    /** List of extensions recognized by this loader */
    private static ExtensionList extensions;

    static final long serialVersionUID =3149080169747384034L;

    /** Creates a new ClassDataLoader */
    public ClassDataLoader () {
        super(REPRESENTATION_CLASS_NAME);
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage (ClassDataLoader.class, "PROP_ClassLoader_Name");
    }
    
     protected String actionsContext () {
        return "Loaders/application/x-java-class/Actions/";               //NOI18N
     }    

    /** For a given file finds a primary file.
    * @param fo the file to find primary file for
    *
    * @return the primary file for the file or null if the file is not
    *   recognized by this loader
    */
    protected FileObject findPrimaryFile (FileObject fo) {
        // never recognize folders.
        if (fo.isFolder())
            return null;
        if (SER_EXT.equals(fo.getExt())) {
            // serialized file, return itself
            try {
                return fo.getFileSystem() != Repository.getDefault().getDefaultFileSystem() ? fo : null;
            } catch (org.openide.filesystems.FileStateInvalidException ex) {
                return null;
            }
        }
        if (CLASS_EXT.equals(fo.getExt())) {
            // class file
            return findPrimaryForClass(fo);
        }
        // not recognized
        return null;
    }

    /** Creates the right data object for given primary file.
    * It is guaranteed that the provided file is realy primary file
    * returned from the method findPrimaryFile.
    *
    * @param primaryFile the primary file
    * @return the data object for this file
    * @exception DataObjectExistsException if the primary file already has data object
    */
    protected MultiDataObject createMultiObject (FileObject primaryFile)
    throws DataObjectExistsException, IOException {
        if (SER_EXT.equals(primaryFile.getExt())) {
            // serialized file, return bean data object
            // moved to ClassDataObject class for performance reasons
            return ClassDataObject.createSerDataObject (primaryFile, this);
        }
        if (CLASS_EXT.equals(primaryFile.getExt())) {
            // class file, return class data object
            // moved to ClassDataObject class for performance reasons
            return ClassDataObject.createCompiledDataObject (primaryFile, this);
        }
        // otherwise
        return null;
    }

    /** Creates the right primary entry for given primary file.
    *
    * @param primaryFile primary file recognized by this loader
    * @return primary entry for that file
    */
    protected MultiDataObject.Entry createPrimaryEntry (MultiDataObject obj, FileObject primaryFile) {
        return new FileEntry(obj, primaryFile);
    }

    /** Creates right secondary entry for given file. The file is said to
    * belong to an object created by this loader.
    *
    * @param secondaryFile secondary file for which we want to create entry
    * @return the entry
    */
    protected MultiDataObject.Entry createSecondaryEntry (MultiDataObject obj, FileObject secondaryFile) {
        return new FileEntry(obj, secondaryFile);
    }

    /** Utility method, finds primary class file for given class file.
    * (input class file can be innerclass class file) */
    private FileObject findPrimaryForClass (final FileObject fo) {
        final String name = fo.getName();
        final int index = name.indexOf(INNER_CLASS_DIVIDER);
        if (index > 0) {
            // could be innerclass class file - try to find outer class file
            FileObject outer =
                fo.getParent().getFileObject(name.substring(0, index), CLASS_EXT);
            if (outer != null) return outer;
        }
        return fo;
    }

    /** @return The list of extensions this loader recognizes
    * (default list constains class, ser extensions)
    */
    public ExtensionList getExtensions () {
        if (extensions == null) {
            extensions = new ExtensionList();
            extensions.addExtension(CLASS_EXT);
            extensions.addExtension(SER_EXT);
        }
        return extensions;
    }
}

