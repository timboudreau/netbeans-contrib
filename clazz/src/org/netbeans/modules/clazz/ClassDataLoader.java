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

