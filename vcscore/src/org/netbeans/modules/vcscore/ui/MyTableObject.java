/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui;

import org.openide.loaders.*;
import org.openide.filesystems.*;

public class MyTableObject {
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property packg. */
    private String packg;
    
    /** Holds value of property filesystem. */
    private String filesystem;
    
    private DataObject dataObject = null;
    
    private FileObject fileObject = null;
    
    public MyTableObject(DataObject dobj) {
        name = dobj.getName();
        packg = dobj.getFolder().getPrimaryFile().getPackageName('/');
        try {
            filesystem = dobj.getPrimaryFile().getFileSystem().getDisplayName();
        } catch (FileStateInvalidException exc) {
            filesystem = "";
        }
        dataObject = dobj;
    }
    
    public MyTableObject(FileObject fo) {
        name = fo.getNameExt();
        packg = fo.getParent().getPackageName('/');
        try {
            filesystem = fo.getFileSystem().getDisplayName();
        } catch (FileStateInvalidException exc) {
            filesystem = "";
        }
        fileObject = fo;
    }
    
    public DataObject getDataObject() {
        return dataObject;
    }
    
    public FileObject getFileObject() {
        return fileObject;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    
    /** Getter for property packg.
     * @return Value of property packg.
     */
    public String getPackg() {
        return this.packg;
    }
    
    
    /** Getter for property filesystem.
     * @return Value of property filesystem.
     */
    public String getFilesystem() {
        return this.filesystem;
    }
    
    
}
