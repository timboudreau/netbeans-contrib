/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
//import java.util.StringTokenizer;

import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.util.WeakListener;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.cache.CacheHandlerEvent;
import org.netbeans.modules.vcscore.cache.CacheHandlerListener;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.versioning.RevisionChildren;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
//import org.netbeans.modules.vcscore.versioning.VcsFileObject;
import org.netbeans.modules.vcscore.versioning.VcsFileStatusEvent;
//import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionChildren;
import org.netbeans.modules.vcscore.versioning.impl.VersioningDataLoader;
//import org.netbeans.modules.vcscore.versioning.impl.AbstractVersioningSystem;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;
//import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionChildren;

/**
 * The VersioningSystem used by VcsFileSystem
 * @author  Martin Entlicher
 */
class VcsVersioningSystem extends VersioningFileSystem implements CacheHandlerListener {

    private VcsFileSystem fileSystem;
    //private VersioningFileSystem.Status status;
    private VersioningFileSystem.Versions versions;
    //private FileStatusListener fileStatus;
    private Hashtable revisionListsByName;

    
    private static final long serialVersionUID = 6349205836150345436L;

    /** Creates new VcsVersioningSystem */
    public VcsVersioningSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        try {
            setSystemName(fileSystem.getSystemName());
        } catch (java.beans.PropertyVetoException vExc) {}
        //this.status = new VersioningFileStatus();
        this.list = fileSystem.getVcsList();
        this.info = fileSystem.getVcsInfo();
        this.attr = new VersioningAttrs();
        this.versions = new VersioningVersions();
        revisionListsByName = new Hashtable();
        initListeners();
    }
    
    private void initListeners() {
        /*
        fileStatus = new FileStatusListener() {
            public void annotationChanged(FileStatusEvent ev) {
                fireFileStatusChanged(ev);
            }
        };
        fileSystem.addFileStatusListener(WeakListener.fileStatus(fileStatus, fileSystem));
         */
    }

    /*
    public AbstractFileSystem.List getList() {
        return fileSystem.getVcsList();
    }
    
    public AbstractFileSystem.Info getInfo() {
        return fileSystem.getVcsInfo();
    }
     */
    
    public FileSystem.Status getStatus() {
        return fileSystem.getStatus();
    }
    
    public VersioningFileSystem.Versions getVersions() {
        return versions;
    }
    
    public FileStatusProvider getFileStatusProvider() {
        return fileSystem.getStatusProvider();
    }
    
    public String getDisplayName() {
        return fileSystem.getDisplayName();
    }
    
    /** Creates Reference. In FileSystem, which subclasses AbstractFileSystem, you can overload method
     * createReference(FileObject fo) to achieve another type of Reference (weak, strong etc.)
     * @param fo is FileObject. It`s reference yourequire to get.
     * @return Reference to FileObject
     */
    protected java.lang.ref.Reference createReference(final FileObject fo) {
        try {
            org.openide.loaders.DataLoaderPool.setPreferredLoader(fo,
                (VersioningDataLoader) org.openide.util.SharedClassObject.findObject(VersioningDataLoader.class, true));
        } catch (java.io.IOException exc) {}
        return super.createReference(fo);
    }
    
    public SystemAction[] getActions(Set vfoSet) {
        return fileSystem.getActions(vfoSet);
        /*
        FileSystem fs = fsInfo.getFileSystem();
        if (fsInfo.canActOnVcsFileObjects()) {
            return fs.getActions(vfoSet);
        } else {
            OrderedSet foSet = new OrderedSet();
            for (Iterator it = vfoSet.iterator(); it.hasNext(); ) {
                VcsFileObject vfo = (VcsFileObject) it.next();
                FileObject fo = fs.findResource(vfo.getPackageName('/'));
                if (fo != null) {
                    foSet.add(fo);
                }
            }
            return fs.getActions(foSet);
        }
         */
    }
    
    /*
    public RevisionChildren createRevisionChildren(RevisionList list) {
        return new NumDotRevisionChildren(list);
    }
     */
    
    public boolean isReadOnly() {
        return false;
    }
    
    private static Object vsActionAccessLock = new Object();

    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        VcsRevisionAction action = (VcsRevisionAction) SystemAction.get(VcsRevisionAction.class);
        synchronized (vsActionAccessLock) {
            action.setFileSystem(fileSystem);
            action.setFileObject(fo);
            action.setSelectedRevisionItems(revisionItems);
        }
        return new SystemAction[] { action };
    }
    
    /*
    public void fireRevisionChange(String name) {
        fireRevisionChange(name, null);
    }
     */
    
    private void vcsStatusChanged (String path, boolean recursively) {
        FileObject fo = findExistingResource(path);
        if (fo == null) return ;
        Enumeration enum = fo.getChildren(recursively);
        HashSet hs = new HashSet();
        while(enum.hasMoreElements()) {
            fo = (FileObject) enum.nextElement();
            hs.add(fo);
            //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
        }
        Set s = Collections.synchronizedSet(hs);
        fireVcsFileStatusChanged(new VcsFileStatusEvent(this, s));
    }

    public void vcsStatusChanged (String name) {
        FileObject fo = findExistingResource(name);
        if (fo == null) return;
        fireVcsFileStatusChanged (new VcsFileStatusEvent(this, Collections.singleton(fo)));
    }
    
    /**
     * is called each time the status of a file changes in cache.
     * The filesystem has to decide wheater it affects him (only in case when
     * there's not the 1-to-1 relationship between cache and fs.
     */
    public void statusChanged(CacheHandlerEvent event) {
        String root = fileSystem.getRootDirectory().getAbsolutePath();
        String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            //D.deb("-------- it is in this filesystem");
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                path = absPath.substring(root.length() + 1, absPath.length());
            }
            path = path.replace(java.io.File.separatorChar, '/');
            if (event.getCacheFile() instanceof org.netbeans.modules.vcscore.cache.CacheDir) {
                vcsStatusChanged(path, event.isRecursive());
            } else {
                vcsStatusChanged(path);
            }
        }
    }
    
    /**
     * is Called when a file/dir is removed from cache.
     */
    public void cacheRemoved(CacheHandlerEvent event) {
    }
    
    /**
     * is called when a file/dir is added to the cache. The filesystem should
     * generally perform findResource() on the dir the added files is in
     * and do refresh of that directory.
     * Note:
     */
    public void cacheAdded(CacheHandlerEvent event) {
    }
    
    /*
    private class VersioningFileStatus extends Object implements VersioningFileSystem.Status {

        public java.lang.String annotateName(java.lang.String displayName, java.util.Set files) {
            return fileSystem.annotateName(displayName, files);
        }
        
        public java.awt.Image annotateIcon(java.awt.Image icon, int iconType, java.util.Set files) {
            return fileSystem.annotateIcon(icon, iconType, files);
        }
        
    }
     */
    
    private class VersioningVersions extends Object implements VersioningFileSystem.Versions {
        
        public VersioningVersions() {
            fileSystem.addRevisionListener(new RevisionListener() {
                public void stateChanged(javax.swing.event.ChangeEvent ev) {
                    //System.out.println("revision state changed:"+ev);
                    if (!(ev instanceof RevisionEvent)) return ;
                    RevisionEvent event = (RevisionEvent) ev;
                    String name = event.getFilePath();
                    //System.out.println("  name = "+name);
                    //public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                    RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                    //System.out.println("old List = "+oldList);
                    if (oldList != null) {
                        RevisionList newList = createRevisionList(name);
                        ArrayList workNew = new ArrayList(newList);
                        synchronized (oldList) {
                            ArrayList workOld = new ArrayList(oldList);
                            workNew.removeAll(oldList);
                            //System.out.println("ADDING new revisions: "+workNew);
                            oldList.addAll(workNew); // add all new revisions
                            workOld.removeAll(newList);
                            //System.out.println("ADDING new revisions: "+workNew);
                            oldList.removeAll(workOld); // remove all old revisions (some VCS may perhaps allow removing revisions)
                            FileStatusProvider status = getFileStatusProvider();
                            if (status != null) {
                                String revision = status.getFileRevision(name);
                                if (revision != null) {
                                    for (Iterator it = oldList.iterator(); it.hasNext(); ) {
                                        RevisionItem item = (RevisionItem) it.next();
                                        item.setCurrent(revision.equals(item.getRevision()));
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        
        public RevisionList getRevisions(String name, boolean refresh) {
            RevisionList list = (RevisionList) revisionListsByName.get(name);//new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList();
            if (list == null || refresh) {
                //org.openide.util.RequestProcessor.postRequest(new Runnable() {
                //    public void run() {
                list = createRevisionList(name);
                if (list != null) revisionListsByName.put(name, list);
                        //versioningSystem.fireRevisionChange(name);
                //    }
                //});
                //System.out.println("createRevisionList("+name+") = "+list);
            }
            //list.add(new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem("1.1"));
            //list.add(new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem("1.2"));
            return list;
        }
        
        private RevisionList createRevisionList(final String name) {
            //System.out.println("createRevisionList("+name+")");
            VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_REVISION_LIST);
            if (cmd == null) return null;
            //VcsCommandExecutor vce = getVcsFactory().getCommandExecutor(cmd, getVariablesAsHashtable());
            Table files = new Table();
            files.put(name, fileSystem.findFileObject(name));
            final StringBuffer dataBuffer = new StringBuffer();
            CommandDataOutputListener dataListener = new CommandDataOutputListener() {
                public void outputData(String[] data) {
                    if (data != null && data.length > 0) {
                        if (data[0] != null) dataBuffer.append(data[0]);
                    }
                }
            };
            VcsCommandExecutor[] vces = VcsAction.doCommand(files, cmd, null, fileSystem, null, null, dataListener, null);
            RevisionList list = null;
            if (vces.length > 0) {
                final VcsCommandExecutor vce = vces[0];
                fileSystem.getCommandsPool().waitToFinish(vce);
                list = getEncodedRevisionList(name, dataBuffer.toString());
            }
            return list;//(RevisionList) revisionListsByName.get(name);
        }
        
        private RevisionList getEncodedRevisionList(final String name, String encodedRL) {
            //System.out.println("addEncodedRevisionList("+name+", "+encodedRL.length()+")");
            if (encodedRL.length() == 0) return null;
            RevisionList list = null;
            try {
                list = (RevisionList) VcsUtilities.decodeValue(encodedRL);
            } catch (java.io.IOException ioExc) {
                //ioExc.printStackTrace();
                list = null;
            }
            return list;
            /*
            if (list != null) {
                /*
                addRevisionListener(new RevisionListener() {
                    public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                        RevisionList newList = createRevisionList(name);
                        RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                        if (oldList != null) {
                            oldList.clear();
                            oldList.addAll(newList);
                        }
                    }
                });
                 *
                revisionListsByName.put(name, list);
            }
             */
            //versioningSystem.fireRevisionChange(name, new RevisionEvent());
        }
        
        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException {
            return fileSystem.inputStream(name);
        }
    }

    /**
     * A very simple implementation of attributes held in memory.
     */
    private static class VersioningAttrs extends Object implements AbstractFileSystem.Attr {
        
        private HashMap files = new HashMap();
        
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
            if (attrs == null) return null;
            else return attrs.get(attrName);
        }
        
    }
    
    /*
    private class FileStatusEventAdapter extends FileStatusEvent {
        
        private FileStatusEvent eventOrig;
        
        public FileStatusEventAdapter(FileStatusEvent event) {
            eventOrig = event;
        }
        
        public FileSystem getFileSystem() {
            return VcsVersioningSystem.this;
        }
        
        public boolean hasChanged(FileObject file) {
            FileObject fileOrig = fileSystem.findFileObject(file.getPackageNameExt('/', '.'));
            if (fileOrig == null) return false;
            return eventOrig.hasChanged(fileOrig);
        }
        
        public boolean isNameChange() {
            return eventOrig.isNameChange();
        }
        
        public boolean isIconChange() {
            return eventOrig.isIconChange();
        }
        
    }
     */
    
}
