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
