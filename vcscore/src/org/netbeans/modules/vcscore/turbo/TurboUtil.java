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
import org.openide.filesystems.FileSystem;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.caching.RefreshCommandSupport;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import java.util.*;

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
        // XXX it would be faster to issue batch command instead of set of commands
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
        // XXX it would be faster to issue batch command instead of set of commands
        for (int i = 0; i < files.length; i++) {
            FileObject fileObject = files[i];
            refreshRecursively(fileObject); // recursion
        }
    }

    /**
     * Populates cache by command output ({@link VcsCache#readDirFinished}).
     *
     * @param fileSystem filesystem that allows to properly match command
     *   output to fileobjects
     * @param path directory that was read by VcsDirReader relative to the filesystem root
     * @param rawData vector of <CODE>String[]</CODE> that describes files and subdirectories
     * @param success whether the refresh command succeeded.
     */
    public static void populateCache(FileSystem fileSystem, String path, Collection rawData, boolean success) {

        // XXX for dir results we can tip local files

//        for (Iterator it = rawData.iterator(); it.hasNext(); ) {
//            String[] elements = (String[]) it.next();
//            String elemName = RefreshCommandSupport.getFileName(elements);
//        }

        // path is folder relative to FS root then raw data contains children
        FileObject folder = fileSystem.findResource(path);    // "" denotes root
        assert folder.isFolder();

        FileObject[] localCandidates = folder.getChildren();
        Iterator it = rawData.iterator();
        while (it.hasNext()) {
            String[] next = (String[]) it.next();
            String fileName = next[0]; // contains trailing '/' (or pathSeparator?) for dirs
            String status = next[1];
            String revision = next[3];

            FileObject fo = folder.getFileObject(fileName);

            if (fo.isData()) {
                FileProperties fprops = new FileProperties();
                fprops.setName(fileName);
                fprops.setStatus(status);
                fprops.setRevision(revision);
                Turbo.setMeta(fo, fprops);
            } else {
                FileProperties fprops = new FileProperties();
                fprops.setName(fileName);
                fprops.setStatus(status);  // TODO what status to set here, we want to say that it's not local
                Turbo.setMeta(fo, fprops);
            }
        }
    }


    /**
     * Returns FileReaderListener implementation that populates
     * the cache from the command data execuded over given FS.
     *
     * @param fs filesystem that allows to properly match command
     *   output to fileobjects
     */
    public static FileReaderListener fileReaderListener(FileSystem fs) {
        return new FileReaderListenerImpl(fs);
    }


    /**
     * Returns DirReaderListener implementation that populates
     * the cache from the command data execuded over given FS.
     *
     * @param fs filesystem that allows to properly match command
     *   output to fileobjects
     */
    public static DirReaderListener dirReaderListener(FileSystem fs) {
        return new DirReaderListenerImpl(fs);
    }

    /**
     * Populates the cache from command output data.
     */
    static final class FileReaderListenerImpl implements FileReaderListener {

        private final FileSystem fileSystem;

        private FileReaderListenerImpl(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
        }

        public void readFileFinished(String path, Collection rawData) {
            populateCache(fileSystem, path, rawData, true);
        }

    }

    static final class DirReaderListenerImpl implements DirReaderListener {

        private final FileSystem fileSystem;

        private DirReaderListenerImpl(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
        }

        public void readDirFinished(String path, Collection rawData, boolean success) {
            populateCache(fileSystem, path, rawData, success);
        }

        // it's typically comming from refresh recursively command all other commands
        // use sequence of readDirFinished or readFileFinished  TODO what's diference? diferent contract for the same purpose?
        public void readDirFinishedRecursive(String path, VcsDirContainer rawData, boolean success) {
            // TODO check this it's taken from sample debuging on linux
            // VcsCache:888

            // path is folder relative to FS root then raw data contains children
            FileObject folder = fileSystem.findResource(path);    // "" denotes root
            assert folder.isFolder();

            Map fileToRawData = (Map) rawData.getElement(); // it's a hashmap<name, rawData.element>
            Collection extractedRawData = fileToRawData.values();
            populateCache(fileSystem, path, extractedRawData, success);

            VcsDirContainer subdirs[] = rawData.getSubdirContainers();
            for (int i = 0; i < subdirs.length; i++) {
                VcsDirContainer container = subdirs[i];
                String containerPath = container.getPath();  // path relative to fs root
                readDirFinishedRecursive(containerPath, container, success);  // recursion
            }
        }
    }
}
