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

package org.netbeans.modules.vcscore.versioning;

import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.EventListenerList;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.versioning.impl.VersioningDataLoader;
import org.netbeans.modules.vcscore.versioning.impl.VersioningFolderDataLoader;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.search.VcsSearchTypeFileSystem;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class VersioningFileSystem extends AbstractFileSystem implements VcsSearchTypeFileSystem {

    private static final SystemAction[] NO_ACTIONS = new SystemAction[0];
    
    //protected FileSystem.Status status;
    //protected VersioningFileSystem.Versions versions;
    
    private AbstractFileSystem fileSystem;
    
    private transient EventListenerList listenerList = new EventListenerList();
    //private transient HashMap revisionListenerMap = new HashMap();
    
    public static interface Versions extends Serializable {
        
        public RevisionList getRevisions(String name, boolean refresh);
        
        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException;
        
    }
    
    private static final long serialVersionUID = 1437726745709092169L;

    public VersioningFileSystem() {
    }
    
    public VersioningFileSystem(AbstractFileSystem underlyingFs) {
        fileSystem = underlyingFs;
//        change = new VersioningFSChange();
//        attr = new VersioningAttrs();
    }
    //public abstract AbstractFileSystem.List getList();
    
    //public abstract AbstractFileSystem.Info getInfo();
    
    //public abstract FileSystem.Status getStatus();
    
    public abstract VersioningFileSystem.Versions getVersions();
    
    /**
     * Create the Children for revisions.
     * @param list the container of RevisionItems or null, when the revisions
     *        are not loaded yet
     */
    //public abstract RevisionChildren createRevisionChildren(RevisionList list);
    
    /**
     * Get the IDE file system associated with this Versioning file system
     * @return the file system or null
     */
    protected FileSystem getFileSystem() {
        return fileSystem;
    }
    
    
    /** Creates Reference. In FileSystem, which subclasses AbstractFileSystem, you can overload method
     * createReference(FileObject fo) to achieve another type of Reference (weak, strong etc.)
     * @param fo is FileObject. It`s reference yourequire to get.
     * @return Reference to FileObject
     */
    protected java.lang.ref.Reference createReference(final FileObject fo) {
        try {
            if (!fo.isFolder()) {
                org.openide.loaders.DataLoaderPool.setPreferredLoader(fo,
                    (VersioningDataLoader) org.openide.util.SharedClassObject.findObject(VersioningDataLoader.class, true));
            }
        } catch (java.io.IOException exc) {}
        java.lang.ref.Reference toReturn = super.createReference(fo);
        try {
            if (fo.isFolder()) {
                org.openide.loaders.DataLoaderPool.setPreferredLoader(fo,
                    (VersioningFolderDataLoader) org.openide.util.SharedClassObject.findObject(VersioningFolderDataLoader.class, true));
            }
        } catch (java.io.IOException exc) {}
        return toReturn;
    }
    

    
    public String getDisplayName() {
        return fileSystem.getDisplayName();
    }
    

    
    public SystemAction[] getActions(Set vfoSet) {
        SystemAction[] actions = fileSystem.getActions(vfoSet);
        SystemAction myAction = SystemAction.get(VersioningExplorerAction.class);
        int index = 0;
        for (; index < actions.length; index++) {
            if (myAction.equals(actions[index])) break;
        }
        if (index < actions.length) {
            SystemAction[] actions1 = new SystemAction[actions.length - 1];
            if (index > 0) {
                System.arraycopy(actions, 0, actions1, 0, index);
            }
            if (index < actions1.length) {
                System.arraycopy(actions, index + 1, actions1, index, actions1.length - index);
            }
            actions = actions1;
        }
        return actions;
        /*  shorter, but probably slower:
        List actionsList = Arrays.asList(actions);
        if (actionsList.contains(myAction)) {
            actionsList.remove(myAction);
            return (SystemAction[]) new ArrayList(actionsList).toArray(new SystemAction[0]);
        } else {
            return actions;
        }
         */
    }
    
    
    /**
     * Get the status provider. All file status information
     * is retrieved from this provider.
     * @return the status provider or <code>null<code>, when no provider
     *         is defined.
     */
    public FileStatusProvider getFileStatusProvider() {
        return null;
    }
        
    /** It should return all possible VCS states in which the files in the filesystem
     * can reside.
     */
    public String[] getPossibleFileStatuses() {
        FileStatusProvider statusProvider = getFileStatusProvider();
        if (statusProvider != null) {
            return (String[]) statusProvider.getPossibleFileStatusesTable().values().toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    /** returns the status for a dataobject. If it matches the status 
     * the user selected in the find dialog (list of all possible states), then
     * it's found and displayed.
     */
    public String getStatus(org.openide.loaders.DataObject dObject) {
        if (dObject instanceof org.netbeans.modules.vcscore.versioning.impl.VersioningDataObject) {
            org.netbeans.modules.vcscore.versioning.impl.VersioningDataObject vdo = 
                (org.netbeans.modules.vcscore.versioning.impl.VersioningDataObject) dObject;
            return vdo.getStatus();
        } else {
            return "";
        }
    }

    /**
     * Perform refresh of status information on all children of a directory
     * @param path the directory path
     * @param recursivey whether to refresh recursively
     */
    public void statusChanged (final String path, final boolean recursively) {
        org.netbeans.modules.vcscore.VcsFileSystem.getStatusChangeRequestProcessor().post(new Runnable() {
            public void run() {
                //D.deb("statusChanged("+path+")"); // NOI18N
                FileObject fo = findResource(path);
                if (fo == null) return;
                //D.deb("I have root = "+fo.getName()); // NOI18N
                Enumeration enum = existingFileObjects(fo);
                //D.deb("I have root = "+fo.getName()); // NOI18N
                //Enumeration enum = fo.getChildren(recursively);
                HashSet hs = new HashSet();
                if (enum.hasMoreElements()) {
                    // First add the root FileObject
                    hs.add(enum.nextElement());
                }
                while(enum.hasMoreElements()) {
                    //fo = (FileObject) enum.nextElement();
                    //hs.add(fo);
                    FileObject chfo = (FileObject) enum.nextElement();
                    if (!fo.equals(chfo.getParent()) && !recursively) break;
                    hs.add(chfo);
                    //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
                }
                Set s = Collections.synchronizedSet(hs);
                fireFileStatusChanged(new FileStatusEvent(VersioningFileSystem.this, s, true, true));
                //checkScheduledStates(s);
            }
        });
    }
    
    /**
     * Perform refresh of status information of a file
     * @param name the full file name
     */
    public void statusChanged (final String name) {
        org.netbeans.modules.vcscore.VcsFileSystem.getStatusChangeRequestProcessor().post(new Runnable() {
            public void run() {
                FileObject fo = findExistingResource(name);
                //System.out.println("findResource("+name+") = "+fo);
                if (fo == null) return;
                fireFileStatusChanged (new FileStatusEvent(VersioningFileSystem.this, fo, true, true));
                //checkScheduledStates(Collections.singleton(fo));
            }
        });
    }
    
    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        return NO_ACTIONS;
    }
    
    /** Finds existing file when its resource name is given.
     * It differs from {@link findResource(String)} method in the fact, that
     * <CODE>null</CODE> is returned if the resource was not created yet.
     * No new file objects are created when looking for the resource.
     * The name has the usual format for the {@link ClassLoader#getResource(String)}
     * method. So it may consist of "package1/package2/filename.ext".
     * If there is no package, it may consist only of "filename.ext".
     *
     * @param name resource name
     *
     * @return VcsFileObject that represents file with given name or
     *   <CODE>null</CODE> if the file was not created yet.
     */
    public FileObject findExistingResource (String name) {
        Enumeration enum = existingFileObjects(getRoot());
        FileObject fo = null;
        while (enum.hasMoreElements()) {
            FileObject obj = (FileObject) enum.nextElement();
            if (name.equals(obj.getPackageNameExt('/', '.'))) {
                fo = obj;
                break;
            }
        }
        return fo;
        //return findResource(name); // TODO
    }
    //public void markImportant(String name, boolean is);
    
    protected void refreshExistingFolders() {
        org.openide.util.RequestProcessor.postRequest(new Runnable() {
            public void run() {
                Enumeration e = existingFileObjects(getRoot());
                while (e.hasMoreElements()) {
                    FileObject fo = (FileObject) e.nextElement();
                    if (fo.isFolder()) {
                        fo.refresh(true);
                    }
                }
            }
        });
    }
    
    public final void addVcsFileStatusListener(VcsFileStatusListener listener) {
        synchronized (listenerList) {
            listenerList.add(VcsFileStatusListener.class, listener);
        }
    }
    
    public final void removeVcsFileStatusListener(VcsFileStatusListener listener) {
        synchronized (listenerList) {
            listenerList.remove(VcsFileStatusListener.class, listener);
        }
    }
    
    protected final void fireVcsFileStatusChanged(VcsFileStatusEvent ev) {
        VcsFileStatusListener[] listeners;
        synchronized (listenerList) {
            listeners = (VcsFileStatusListener[]) listenerList.getListeners(VcsFileStatusListener.class);
        }
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].vcsStatusChanged(ev);
        }
    }
    
    /**
     * A very simple implementation of attributes held in memory.
     */
    public static class VersioningAttrs extends Object implements AbstractFileSystem.Attr {
        
        private HashMap files = new HashMap();
        
        private static final long serialVersionUID = 7177122547454760079L;
        
        public void deleteAttributes(String name) {
            files.remove(name);
        }
        
        public java.util.Enumeration attributes(String name) {
            HashMap attrs = (HashMap) files.get(name);
            if (attrs == null) {
                return new org.openide.util.enum.EmptyEnumeration();
            } else {
                return Collections.enumeration(attrs.keySet());
            }
        }
        
        public void renameAttributes(String oldName, String newName) {
            HashMap attrs = (HashMap) files.get(oldName);
            if (attrs != null) {
                files.remove(oldName);
                files.put(newName, attrs);
            }
        }
        
        public void writeAttribute(String name, String attrName, Object value) throws java.io.IOException {
            HashMap attrs = (HashMap) files.get(name);
            if (attrs == null) attrs = new HashMap();
            attrs.put(attrName, value);
            files.put(name, attrs);
        }
        
        public java.lang.Object readAttribute(String name, String attrName) {
            HashMap attrs = (HashMap) files.get(name);
            Object toReturn = null;
            if (attrs == null) toReturn =  null;
            else toReturn = attrs.get(attrName);
            return toReturn;
        }
        
    }
    
    public class VersioningFSChange extends Object implements AbstractFileSystem.Change {

        private static final long serialVersionUID = -4757075426649682071L;
        
        public void delete(String name) throws java.io.IOException {
            FileObject fo = fileSystem.findResource(name);
            if (fo != null) {
                fo.delete(fo.lock());
                //fileSystem.delete(name);
            } else {
                throw new java.io.IOException(NbBundle.getMessage(VersioningFileSystem.class, "Exc_FileCanNotDelete", name));
            }
        }

        public void createFolder(String path) throws java.io.IOException {
            String dir = VcsUtilities.getDirNamePart(path);
            String file = VcsUtilities.getFileNamePart(path);
            FileObject fo = fileSystem.findResource(dir);
            if (fo != null) {
                fo.createFolder(file);
            } else {
                throw new java.io.IOException(NbBundle.getMessage(VersioningFileSystem.class, "Exc_FolderCanNotCreate", path));
            }
        }

        public void createData(String path) throws java.io.IOException {
            String dir = VcsUtilities.getDirNamePart(path);
            String file = VcsUtilities.getFileNamePart(path);
            FileObject fo = fileSystem.findResource(dir);
            if (fo != null) {
                fo.createData(file);
            } else {
                throw new java.io.IOException(NbBundle.getMessage(VersioningFileSystem.class, "Exc_FileCanNotCreate", path));
            }
        }

        public void rename(String oldName, String newName) throws java.io.IOException {
            int extIndex = newName.lastIndexOf('.');
            if (extIndex >= 0) {
                // sort of a hack: the VersioningDataNode adds the file extension to it's name.
                //                 Therefore we need to remove it here otherwise it would be added twise.
                newName = newName.substring(0, extIndex);
            }
            String oldDir = VcsUtilities.getDirNamePart(oldName);
            String oldFile = VcsUtilities.getFileNamePart(oldName);
            String newFile = VcsUtilities.getFileNamePart(newName);
            String newFileExt;
            extIndex = newFile.lastIndexOf('.');
            if (extIndex >= 0) {
                newFileExt = newFile.substring(extIndex + 1);
                newFile = newFile.substring(0, extIndex);
            } else {
                newFileExt = null;
            }
            FileObject fo = fileSystem.findResource(oldName);
            if (fo != null) {
                fo.rename(fo.lock(), newFile, newFileExt);
            } else {
                throw new java.io.IOException(NbBundle.getMessage(VersioningFileSystem.class, "Exc_FileCanNotRename", oldName));
            }
        }

    }
    
    public class DefVersioningList extends Object implements AbstractFileSystem.List {
        
        private static final long serialVersionUID = 567851736120604546L;
        
        public java.lang.String[] children(java.lang.String str) {
            FileObject fo = fileSystem.findResource(str);
            FileObject[] childs = fo.getChildren();
            if (childs != null) {
                String[] toReturn = new String[childs.length];
                for (int i = 0; i < childs.length; i++) {
                    toReturn[i] = childs[i].getNameExt();
                }
                return toReturn;
            } else {
                return new String[0];
            }
        }
        
    }

    
}
