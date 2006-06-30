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

    private static String getPackageNameSlashes(FileObject fo) {
        String path = fo.getPath();
        int i = path.lastIndexOf('.');
        if (i != -1 && i > path.lastIndexOf('/')) {
            path = path.substring(0, i);
        }
        return path;
    }

    public MyTableObject(DataObject dobj) {
        name = dobj.getName();
        packg = getPackageNameSlashes(dobj.getFolder().getPrimaryFile());
        try {
            filesystem = dobj.getPrimaryFile().getFileSystem().getDisplayName();
        } catch (FileStateInvalidException exc) {
            filesystem = "";
        }
        dataObject = dobj;
    }
    
    public MyTableObject(FileObject fo) {
        name = fo.getNameExt();
        packg = getPackageNameSlashes(fo.getParent());
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
