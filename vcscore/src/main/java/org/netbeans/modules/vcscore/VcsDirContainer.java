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

package org.netbeans.modules.vcscore;

import java.util.*;

import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * Container for objects belonging to directories.
 * Is needed to store data when downloading directory recursively.
 *
 * @author  Martin Entlicher
 * @version
 */
public class VcsDirContainer extends Object {

    private Vector subdirs = new Vector();
    //private Vector elements = new Vector();
    private Object element = null;

    private String path = ""; // NOI18N
    private String name = ""; // NOI18N

    /** Creates new empty VcsDirContainer */
    public VcsDirContainer() {
    }

    /** Creates new VcsDirContainer with given path.
     * @param path the directory path.
     */
    public VcsDirContainer(String path) {
        this.path = path;
        this.name = VcsUtilities.getFileNamePart(path);
    }

    /**
     * Get the directory path.
     * @return the directory path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the directory path.
     * @param path the directory path
     */
    public void setPath(String path) {
        this.path = path;
        this.name = VcsUtilities.getFileNamePart(path);
    }

    /**
     * Get the name of this directory.
     * @return the name of this directory
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this directory.
     * @param name the name of this directory
     */
    public void setName(String name) {
        this.name = name;
    }

    //public void addElement(Object element) {
    //  elements.addElement(element);
    //}

    //public Vector getElements() {
    //  return elements;
    //}

    /**
     * Set the element object belonging to this directory.
     * @param element the element to assign
     */
    public void setElement(Object element) {
        this.element = element;
    }

    /**
     * Get the element belonging to this directory.
     * @return the element or <code>null</code> for empty folders.
     */
    public Object getElement() {
        return element;
    }

    /**
     * Get the subdirectory with the given path.
     * @return the subdirectory or null if does not exist.
     */
    public VcsDirContainer getSubdir(String path){
        int subdirsLen = subdirs.size();
        VcsDirContainer subdir = null;
        for(int i = 0; i < subdirsLen; i++){
            VcsDirContainer dir = (VcsDirContainer) subdirs.elementAt(i);
            if (path.equals(dir.getPath())) subdir = dir;
        }
        return subdir;
    }

    /**
     * Get all subdirectories.
     * @return the array of subdirectories.
     */
    public VcsDirContainer[] getSubdirContainers() {
        int subdirsLen = subdirs.size();
        VcsDirContainer[] subDirs = new VcsDirContainer[subdirsLen];
        for(int i = 0; i < subdirsLen; i++){
            subDirs[i] = (VcsDirContainer) subdirs.elementAt(i);
        }
        return subDirs;
    }

    /**
     * Add a subdirectory with the given path.
     * @param path the path of new directory
     * @return new directory container
     */
    public VcsDirContainer addSubdir(String path) {
        VcsDirContainer dir = getSubdir(path);
        if (dir == null) {
            dir = new VcsDirContainer(path);
            subdirs.addElement(dir);
        }
        return dir;
    }

    /**
     * Add a subdirectory tree with the given path. Necessary intermediate
     * directories are created.
     * @param path the path of new directory
     * @return new directory container or null when the path is bad
     */
    public VcsDirContainer addSubdirRecursive(String path) {
        if (this.path.equals(path)) return this;
        int index;
        if (this.path.length() > 0) {
            index = path.indexOf(this.path);
            if (index < 0) {
                return null;
            }
            index += this.path.length() + 1; // have to cross the path delimeter
        } else {
            index = 0;
        }
        int index2 = path.indexOf('/', index);
        if (index2 < 0) index2 = path.length();
        if (index2 < index) return this;
        String next = path.substring(index, index2);
        String subPath = (this.path.length() > 0) ? this.path+"/"+next : next; // NOI18N
        VcsDirContainer subdir = this.getDirContainer(next);
        if (subdir == null) {
            subdir = this.addSubdir(subPath);
        }
        return subdir.addSubdirRecursive(path);
    }
    //public Vector getSubdirs() {
    //  return subdirs;
    //}

    /**
     * Get all subdirectories of this directory.
     * @return an array of subdirectories' names
     */
    public String[] getSubdirs(){
        int subdirsLen = subdirs.size();
        String[] res = new String[subdirsLen];
        for(int i = 0; i < subdirsLen; i++){
            VcsDirContainer dir = (VcsDirContainer) subdirs.elementAt(i);
            res[i] = dir.getName();
        }
        return res;
    }

    /**
     * Get the directory container of subdirectory of the given name.
     * @param name the directory name to look for
     * @return the directory container of the given name, or null when
     *         the directory name does not exist.
     */
    public VcsDirContainer getDirContainer(String name){
        VcsDirContainer dir = null;
        for(int i = 0; i < subdirs.size(); i++){
            dir = (VcsDirContainer) subdirs.elementAt(i);
            if (dir.getName().equals(name)) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Get the container of the given path.
     * @param path
     * return the container of the given path or null when not found
     */
    public VcsDirContainer getContainerWithPath(String path) {
        VcsDirContainer container = this;
        String rootPath = container.getPath();
        if (rootPath.length() > 0 && path.indexOf(rootPath) < 0) return null;
        if (path.length() >= 0 && path.equals(rootPath)) return this;
        int index = rootPath.length();
        if (index > 0) index++; // we have to cross the file separator
        int indexSep = path.indexOf('/', index);
        if (indexSep < 0) indexSep = path.length();
        while (indexSep >= 0 && container != null) {
            String name = path.substring(index, indexSep);
            container = container.getDirContainer(name);
            index = indexSep + 1;
            if (index >= path.length()) indexSep = -1;
            else {
                indexSep = path.indexOf('/', index);
                if (indexSep < 0) indexSep = path.length();
            }
        }
        return container;
    }

    /**
     * Get the parent directory container.
     * @param path the directory path of which the container we are looking for
     * @return the parent directory container, or null when not found
     */
    public VcsDirContainer getParent(String path) {
        String parentPath = VcsUtilities.getDirNamePart(path);
        VcsDirContainer container = getContainerWithPath(parentPath);
        return container;
    }
}
