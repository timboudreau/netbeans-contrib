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

package org.netbeans.modules.vcscore;

import java.util.*;

import org.netbeans.modules.vcscore.util.MiscStuff;
import org.netbeans.modules.vcscore.util.Debug;

/**
 * Container for objects belonging to directories.
 * Is needed to store data when downloading directory recursively.
 *
 * @author  Martin Entlicher
 * @version 
 */
public class VcsDirContainer extends Object {
    private Debug D = new Debug("VcsDirContainer", true); // NOI18N

    private Vector subdirs = new Vector();
    //private Vector elements = new Vector();
    private Object element = null;

    private String path = ""; // NOI18N
    private String name = "";

    /** Creates new empty VcsDirContainer */
    public VcsDirContainer() {
    }

    /** Creates new VcsDirContainer with given path.
     * @param path the directory path.
     */
    public VcsDirContainer(String path) {
        this.path = path;
        this.name = MiscStuff.getFileNamePart(path);
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
        this.name = MiscStuff.getFileNamePart(path);
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
     * @return the element.
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
                D.deb("addSubdirRecursive("+path+"): indexOf("+this.path+") = "+index+" => RETURN null !!!");
                return null;
            }
            index += this.path.length() + 1; // have to cross the path delimeter
        } else {
            index = 0;
        }
        int index2 = path.indexOf('/', index);
        if (index2 < 0) index2 = path.length();
        //D.deb("index = "+index+", index2 = "+index2);
        if (index2 < index) return this;
        String next = path.substring(index, index2);
        //D.deb("next = "+next);
        String subPath = (this.path.length() > 0) ? this.path+"/"+next : next;
        VcsDirContainer subdir = this.getDirContainer(next);
        if (subdir == null) {
            D.deb("addSubdirRecursive("+path+"): creating subdir "+subPath+" under "+this.path);
            subdir = this.addSubdir(subPath);
        } else D.deb("addSubdirRecursive("+path+"): exist subdir "+subPath+" under "+this.path);
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
        D.deb("getContainerWithPath("+path+")");
        VcsDirContainer container = this;
        if (path.length() == 0) return container;
        String rootPath = container.getPath();
        //D.deb("parentPath = "+parentPath+", rootPath = "+rootPath);
        if (rootPath.length() > 0 && path.indexOf(rootPath) < 0) return null;
        if (path.length() > 0 && path.equals(rootPath)) return this;
        D.deb("getContainerWithPath: rootPath = '"+rootPath+"'");
        int index = rootPath.length();
        if (index > 0) index++; // we have to cross the file separator
        int indexSep = path.indexOf('/', index);
        if (indexSep < 0) indexSep = path.length();
        while (indexSep >= 0 && container != null) {
            String name = path.substring(index, indexSep);
            //D.deb("name = "+name);
            container = container.getDirContainer(name);
            index = indexSep + 1;
            if (index >= path.length()) indexSep = -1;
            else {
                indexSep = path.indexOf('/', index);
                if (indexSep < 0) indexSep = path.length();
            }
        }
        D.deb("getContainerWithPath("+path+") returning "+((container == null) ? null : container.getPath()));
        return container;
    }

    /**
     * Get the parent directory container.
     * @param path the directory path of which the container we are looking for
     * @return the parent directory container, or null when not found
     */
    public VcsDirContainer getParent(String path) {
        //org.netbeans.modules.vcs.util.Debug D =
        //  new org.netbeans.modules.vcs.util.Debug("VcsDirContainer", true); // NOI18N
        //D.deb("getParent("+path+")");
        String parentPath = MiscStuff.getDirNamePart(path);
        VcsDirContainer container = getContainerWithPath(parentPath);
        D.deb("getParent("+path+") returning "+((container == null) ? null : container.getPath()));
        return container;
        /*
        String rootPath = container.getPath();
        //D.deb("parentPath = "+parentPath+", rootPath = "+rootPath);
        if (rootPath.length() > 0 && parentPath.indexOf(rootPath) < 0) return null;
        int index = rootPath.length();
        if (index > 0) index++; // we have to cross the file separator
        int indexSep = path.indexOf('/', index);
        while (indexSep >= 0 && container != null) {
          String name = parentPath.substring(index, indexSep);
          //D.deb("name = "+name);
          container = container.getDirContainer(name);
          index = indexSep + 1;
          indexSep = path.indexOf('/', index);
    }
        //D.deb("getParent returning "+((container == null) ? null : container.getPath()));
        return container;
        */
    }
}
