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
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Various utility methods eliminating boilerplate constructions
 * over {@link Turbo} API goes here.
 *
 * @author Petr Kuzel
 */
public final class TurboUtil {

    public static FileObject[] listFolders(FileObject fileObject) {
        FileObject fo[] = fileObject.getChildren();
        List ret = new ArrayList(fo.length);
        for (int i = 0; i<fo.length; i++) {
            if (fo[i].isFolder()) {
                ret.add(fo[i]);
            }
        }
        return (FileObject[]) ret.toArray(new FileObject[ret.size()]);
    }

    /**
     * Iterates over folder children and updates status from repository
     */
    public static void refreshFolder(FileObject folder) {
        if (folder.isFolder() == false) return;
        FileObject[] files = folder.getChildren();
        for (int i = 0; i < files.length; i++) {
            FileObject fileObject = files[i];
            Turbo.getRepositoryMeta(fileObject);
        }
    }

    /**
     * Recursively iterates over all children and updates status from repository
     */
    public static void refreshRecursively(FileObject folder) {
        Turbo.getRepositoryMeta(folder);
        if (folder.isFolder() == false) return;

        FileObject[] files = folder.getChildren();
        for (int i = 0; i < files.length; i++) {
            FileObject fileObject = files[i];
            refreshRecursively(fileObject); // recursion
        }
    }

    /**
     * Populates cache by command output ({@link VcsCache#readDirFinished}).
     *
     * @param path directory that was read by VcsDirReader relative to the filesystem root
     * @param rawData vector of <CODE>String[]</CODE> that describes files and subdirectories
     * @param success whether the refresh command succeeded.
     */
    public static void populateCache(String path, Collection rawData, boolean success) {

    }


    /**
     * Returns FileReaderListener implementation that populates
     * the cache from the command data.
     */
    public static FileReaderListener fileReaderListener() {
        return FileReaderListenerImpl.getInstance();
    }


    /**
     * Returns DirReaderListener implementation that populates
     * the cache from the command data.
     */
    public static DirReaderListener dirReaderListener() {
        return DirReaderListenerImpl.getInstance();
    }

    /**
     * Populates the cache from command output data.
     */
    static final class FileReaderListenerImpl implements FileReaderListener {

        private static FileReaderListener instance;

        private FileReaderListenerImpl() {
        }

        public synchronized static FileReaderListener getInstance() {
            if (instance == null) {
                instance = new FileReaderListenerImpl();
            }
            return instance;
        }

        public void readFileFinished(String path, Collection rawData) {
            // TODO what is rawData format? can I delegate to populateCache?
        }

    }

    static final class DirReaderListenerImpl implements DirReaderListener {

        private static DirReaderListener instance;

        private DirReaderListenerImpl() {
        }

        public synchronized static DirReaderListener getInstance() {
            if (instance == null) {
                instance = new DirReaderListenerImpl();
            }
            return instance;
        }

        public void readDirFinished(String path, Collection rawData, boolean success) {
            // TODO what is rawData format? can I delegate to populateCache?
        }

        public void readDirFinishedRecursive(String path, VcsDirContainer rawData, boolean success) {
            // TODO what is rawData format? can I delegate to populateCache?
        }
    }

}
