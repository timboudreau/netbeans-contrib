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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
//import java.util.StringTokenizer;

import org.openide.filesystems.AbstractFileSystem;
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
import org.netbeans.modules.vcscore.versioning.VersioningSystem;
import org.netbeans.modules.vcscore.versioning.VcsFileObject;
import org.netbeans.modules.vcscore.versioning.VcsFileStatusEvent;
import org.netbeans.modules.vcscore.versioning.impl.AbstractVersioningSystem;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;
//import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionChildren;

/**
 * The VersioningSystem used by VcsFileSystem
 * @author  Martin Entlicher
 */
class VcsVersioningSystem extends AbstractVersioningSystem implements CacheHandlerListener {

    private VcsFileSystem fileSystem;
    private VersioningSystem.Status status;
    private VersioningSystem.Versions versions;
    private FileStatusListener fileStatus;
    private Hashtable revisionListsByName;

    
    /** Creates new VcsVersioningSystem */
    public VcsVersioningSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        try {
            setSystemName(fileSystem.getSystemName());
        } catch (java.beans.PropertyVetoException vExc) {}
        this.status = new VersioningStatus();
        this.versions = new VersioningVersions();
        revisionListsByName = new Hashtable();
        initListeners();
    }
    
    private void initListeners() {
        fileStatus = new FileStatusListener() {
            public void annotationChanged(FileStatusEvent ev) {
                fireFileStatusChanged(ev);
            }
        };
        fileSystem.addFileStatusListener(WeakListener.fileStatus(fileStatus, fileSystem));
    }

    public AbstractFileSystem.List getList() {
        return fileSystem.getVcsList();
    }
    
    public AbstractFileSystem.Info getInfo() {
        return fileSystem.getVcsInfo();
    }
    
    public VersioningSystem.Status getStatus() {
        return status;
    }
    
    public VersioningSystem.Versions getVersions() {
        return versions;
    }
    
    public FileStatusProvider getFileStatusProvider() {
        return fileSystem.getStatusProvider();
    }
    
    public String getDisplayName() {
        return fileSystem.getDisplayName();
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
    
    private static Object vsActionAccessLock = new Object();

    public SystemAction[] getRevisionActions(VcsFileObject fo, Set revisionItems) {
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
        VcsFileObject fo = findExistingResource(path);
        if (fo == null) return ;
        Enumeration enum = fo.getChildren(recursively);
        HashSet hs = new HashSet();
        while(enum.hasMoreElements()) {
            fo = (VcsFileObject) enum.nextElement();
            hs.add(fo);
            //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
        }
        Set s = Collections.synchronizedSet(hs);
        fireVcsFileStatusChanged(new VcsFileStatusEvent(this, s));
    }

    public void vcsStatusChanged (String name) {
        VcsFileObject fo = findExistingResource(name);
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
    
    private class VersioningStatus extends Object implements VersioningSystem.Status {
        public String annotateName(String fileName, String displayName) {
            return fileSystem.annotateName(fileName, displayName);
        }
        
        public java.awt.Image annotateIcon(String fileName, java.awt.Image icon, int iconType) {
            return fileSystem.annotateIcon(icon, iconType, fileName);
        }
    }
    
    private class VersioningVersions extends Object implements VersioningSystem.Versions {
        
        public VersioningVersions() {
            fileSystem.addRevisionListener(new RevisionListener() {
                public void stateChanged(javax.swing.event.ChangeEvent ev) {
                    System.out.println("revision state changed:"+ev);
                    if (!(ev instanceof RevisionEvent)) return ;
                    RevisionEvent event = (RevisionEvent) ev;
                    String name = event.getFilePath();
                    System.out.println("  name = "+name);
                    //public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                    RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                    System.out.println("old List = "+oldList);
                    if (oldList != null) {
                        RevisionList newList = createRevisionList(name);
                        ArrayList workNew = new ArrayList(newList);
                        synchronized (oldList) {
                            ArrayList workOld = new ArrayList(oldList);
                            workNew.removeAll(oldList);
                            oldList.addAll(workNew); // add all new revisions
                            workOld.removeAll(newList);
                            oldList.removeAll(workOld); // remove all old revisions (some VCS may perhaps allow removing revisions)
                            FileStatusProvider status = getFileStatusProvider();
                            if (status != null) {
                                String revision = status.getFileRevision(name);
                                for (Iterator it = oldList.iterator(); it.hasNext(); ) {
                                    RevisionItem item = (RevisionItem) it.next();
                                    item.setCurrent(revision.equals(item.getRevision()));
                                }
                            }
                            //oldList.clear();
                            //oldList.addAll(newList);
                        }
                    //} else {
                    //    revisionListsByName.put(name, newList);
                    }
                }
            });
        }
        
        public RevisionList getRevisions(final String name) {
            RevisionList list = (RevisionList) revisionListsByName.get(name);//new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList();
            if (list == null) {
                //org.openide.util.RequestProcessor.postRequest(new Runnable() {
                //    public void run() {
                list = createRevisionList(name);
                revisionListsByName.put(name, list);
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
    
}
