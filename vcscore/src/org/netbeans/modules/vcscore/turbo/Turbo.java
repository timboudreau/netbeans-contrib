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
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.File;

/**
 * Client code main entry point.
 *
 * @author Petr Kuzel
 */
public final class Turbo {

    private static TurboListener[] listeners = new TurboListener[0];

    private static final Turbo SINGLETON = new Turbo();

    private static volatile boolean justFiring = false;

    private Turbo() {
    }

    /**
     * Request last known status. If called from AWT it
     * tries to locate data in memory layer. If it does
     * not find any it returns null and schedules background
     * meta retrieval. If called from other threads
     * it tries disk and possibly repository layer too.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getMeta(FileObject fileObject) {

        if (fileObject == null) return null;

        if (SwingUtilities.isEventDispatchThread()) {
            FileProperties fprops = Memory.get(fileObject);
            if (fprops != null) return fprops;
            prepareMeta(fileObject);
            return null;
        } else {
            FileProperties fprops = getCachedMeta(fileObject);
            if (fprops != null) {
                return fprops;
            } else {
                return getRepositoryMeta(fileObject);
            }
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
        // do not invalidate cached data if repository returns null due to command failure
        if (fprops != null) setMeta(fileObject, fprops);
        return fprops;
    }

    /**
     * Instruct system to load data from slow layers. Data are actualy delivered
     * sometimes in future. It's possible to attach a listener to get notified.
     *
     * @param fileObject
     */
    public static void prepareMeta(FileObject fileObject) {
        if (fileObject == null) return;
        Disk.prepareMeta(fileObject);
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
     * Populate cache by given attributes. Makes them immutable.
     * @param status new status or <code<null</code> for invalidating
     */
    public static void setMeta(FileObject fileObject, FileProperties status) {
        assert justFiring == false;
        if (fileObject == null) return;
        if (status != null) status.freeze();
        Memory.put(fileObject, status);
        Disk.put(fileObject, status);
    }

    /**
     * Populates the cache by given attributes. It tries to locate
     * live fileobject. If it fails it silently stores the status
     * without distributing change event.
     * @param file
     */
    public static void setMeta(File file, FileProperties status) {
        assert justFiring == false;
        status.freeze();
        FileObject fo = Memory.getLiveFileObject(file);
        if (fo != null) {
            setMeta(fo, status);
        } else {
            Memory.putDeadFile(file, status);
            Disk.put(file, status);
        }
    }

    public void addTurboListener(TurboListener l) {
        List clone = new ArrayList(Arrays.asList(listeners));
        clone.add(l);
        listeners = (TurboListener[]) clone.toArray(new TurboListener[clone.size()]);
    }

    public void removeTurboListener(TurboListener l) {
        List clone = new ArrayList(Arrays.asList(listeners));
        clone.remove(l);
        listeners = (TurboListener[]) clone.toArray(new TurboListener[clone.size()]);
    }

    static void fireTurboEvent(FileObject fileObject, FileProperties status) {
        try {
            justFiring = true;
            if (listeners.length > 0) {
                TurboEvent e = new TurboEvent(fileObject, status);
                for (int i=0; i<listeners.length; i++) {
                    listeners[i].turboChanged(e);
                }
            }
        } finally {
            justFiring = false;
        }
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

    /**
     * You do not need this until you need add event listeners. There are static
     * methods for all other oprations. Listeners must be added on default
     * instance in order to support WeakListeners. WeakListeners
     * are crucial here as Turbo's lifetime (it's static) exceeds
     * lifetime of most potentional listeners.
     */
    public static Turbo singleton() {
        return SINGLETON;
    }

}
