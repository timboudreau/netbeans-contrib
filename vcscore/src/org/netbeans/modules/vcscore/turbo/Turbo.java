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

import javax.swing.*;

/**
 * Client code main entry point.
 *
 * @author Petr Kuzel
 */
public final class Turbo {

    /**
     * Request last known status. It can contact repository if
     * not cached. In such case it can block forever. Hence it must
     * not be called from AWT.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getMeta(FileObject fileObject) {

        if (fileObject == null) return null;

        FileProperties fprops = getCachedMeta(fileObject);
        if (fprops != null) {
            return fprops;
        } else {
            return getRepositoryMeta(fileObject);
        }
    }

    /**
     * Request fresh status from repository. May block forever.
     * As side effect the status is cached. Hence it must
     * not be called from AWT.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getRepositoryMeta(FileObject fileObject) {

        assert SwingUtilities.isEventDispatchThread() == false;

        if (fileObject == null) return null;

        FileProperties fprops = Repository.get(fileObject);
        setMeta(fileObject, fprops);
        return fprops;
    }

    /**
     * Request cached status. Handled promptly but can return
     * unknown rather often.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getCachedMeta(FileObject fileObject) {
        if (fileObject == null) return null;

        FileProperties fprops = Memory.get(fileObject);
        if (fprops != null) return fprops;

        assert SwingUtilities.isEventDispatchThread() == false;

        fprops = Disk.get(fileObject);
        if (fprops != null) {
            Memory.put(fileObject, fprops);
            return fprops;
        }

        return null;
    }

    /**
     * Populate cache by given attributes.
     */
    public static void setMeta(FileObject fileObject, FileProperties status) {
        Memory.put(fileObject, status);
        Disk.put(fileObject, status);
    }

    /**
     * AbstractFileSystem allows hooks using customized
     * FileObject references. It should be called by AbstractFileSystems
     * subclasses only.
     */
    public static FileReference createFileReference(FileObject fileObject) {
        return new FileReference(fileObject);
    }

    /**
     * For testing purposes only. It allows to temporary keep two
     * cache implementations. E.g. client code that does no break
     * indentation of the old code:
     * <pre>
     *   if (Turbo.implemented()) {
     *       // use cool approach
     *       return;
     *   } // else use old cumbersome approach
     * </pre>
     * @deprecated client's code should use Turbo unconditionally...
     */
    public static boolean implemented() {
        return false;
    }
}
