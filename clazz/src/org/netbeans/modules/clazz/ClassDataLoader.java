/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
    
    protected synchronized SystemAction[] defaultActions() {
        return new SystemAction [] {
            SystemAction.get(FileSystemAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(SaveAsTemplateAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
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

