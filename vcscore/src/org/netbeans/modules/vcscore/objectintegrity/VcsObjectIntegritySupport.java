/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.objectintegrity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.OperationAdapter;
import org.openide.loaders.OperationEvent;
import org.openide.loaders.OperationListener;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListener;

import org.netbeans.modules.vcscore.FileObjectExistence;
import org.netbeans.modules.vcscore.FileObjectImportantness;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.cache.CacheDir;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.cache.CacheHandlerEvent;
import org.netbeans.modules.vcscore.cache.CacheHandlerListener;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.util.Table;

import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 * The support for keeping the integrity of DataObjects in VCS repository.
 *
 * @author  Martin Entlicher
 */
public class VcsObjectIntegritySupport extends OperationAdapter implements Runnable,
                                                                           CacheHandlerListener,
                                                                           Serializable {
    
    /**
     * The name of the FileObject attribute under which this object integrity
     * support is stored.
     */
    public static final String ATTRIBUTE_NAME = VcsObjectIntegritySupport.class.getName();
    
    /**
     * This property is fired when the file names change.
     */
    public static final String PROPERTY_FILES_CHANGED = "filesChanged"; // NOI18N
    
    private static final int ANALYZER_SCHEDULE_TIME = 200;
    private static final RequestProcessor analyzerRequestProcessor = new RequestProcessor("VCS Object Integrity Analyzer", 1);
    
    private transient FileSystem fileSystem;
    private transient FileSystemCache cache;
    private transient String fsRootPath;
    private transient FileObjectExistence foExistence;
    private transient FileObjectImportantness foImportantness;
    /** The DataObjects which need to be analyzed for objects integrity. */
    private transient Set objectsToAnalyze;
    /** The paths of FileObjects whose DataObjects need to be analyzed for objects integrity. */
    private transient Set pathsToAnalyze;
    private transient RequestProcessor.Task analyzerTask;
    private transient boolean activated = false;
    
    private transient OperationListener operationListener;
    private transient CacheHandlerListener cacheHandlerListaner;
    private transient PropertyChangeSupport propertyChangeSupport;
    private transient PropertyChangeListener doFileChangeListener;
    
    //private List localFileNames = new ArrayList();
    /** A map of set of names of local secondary files by names of primary files. */
    private Map objectsWithLocalFiles = new HashMap();
    /** A map of primary file names by local secondary files. */
    private Map filesMap = new HashMap();
    /** The set of names of primary files, that are local. They might become non-local later. */
    private Set primaryLocalFiles = new HashSet();
    /** The set of names of secondary files, that are local, but ignored. */
    private Set ignoredSecondaryLocalFiles = Collections.synchronizedSet(new HashSet());
    
    private static final long serialVersionUID = 7128452018671390570L;
    /** Creates a new instance of VcsObjectIntegritySupport.
     * It's unusable until it's activated.
     */
    public VcsObjectIntegritySupport() {
        //System.out.println("VOIS Created.");
    }
    
    /**
     * Tells whether this object integrity support was already activated.
     */
    public synchronized boolean isActivated() {
        return activated;
    }
    
    /**
     * Activate this object integrity support.
     * @param fileSystem The VCS FileSystem on which the object integrity is kept.
     * @param cache The status cache, that is used to check the files status information.
     * @param fsRootPath The path of the filesystem root.
     * @param foImportantness The information about which FileObjects are important.
     */
    public synchronized void activate(FileSystem fileSystem, FileSystemCache cache,
                                      String fsRootPath,
                                      FileObjectExistence foExistence,
                                      FileObjectImportantness foImportantness) {
        this.fileSystem = fileSystem;
        this.cache = cache;
        this.fsRootPath = fsRootPath;
        this.foExistence = foExistence;
        this.foImportantness = foImportantness;
        this.objectsToAnalyze = new HashSet();
        this.pathsToAnalyze = new HashSet();
        this.analyzerTask = analyzerRequestProcessor.post(this, ANALYZER_SCHEDULE_TIME, Thread.MIN_PRIORITY);
        //analyzerTask.setPriority(Thread.MIN_PRIORITY);
        DataLoaderPool pool = (DataLoaderPool) Lookup.getDefault().lookup(DataLoaderPool.class);
        operationListener = (OperationListener) WeakListener.create(OperationListener.class, this, pool);
        pool.addOperationListener(operationListener);
        cacheHandlerListaner = (CacheHandlerListener) WeakListener.create(CacheHandlerListener.class, this, cache);
        cache.addCacheHandlerListener(cacheHandlerListaner);
        propertyChangeSupport = new PropertyChangeSupport(this);
        doFileChangeListener = new DOFileChangeListener();
        this.activated = true;
        //System.out.println("VOIS Activated for ("+fsRootPath+"):"+fileSystem);
    }
    
    /**
     * Deactivate this object integrity support.
     */
    public void deactivate() {
        synchronized (this) {
            if (operationListener != null) {
                DataLoaderPool pool = (DataLoaderPool) Lookup.getDefault().lookup(DataLoaderPool.class);
                pool.removeOperationListener(operationListener);
            }
            if (cacheHandlerListaner != null) {
                cache.removeCacheHandlerListener(cacheHandlerListaner);
            }
        }
        // We must allow that the analyzar task can fire property changes,
        // so this must be outside of the synchronized block!
        analyzerTask.waitFinished();
        synchronized (this) {
            propertyChangeSupport = null;
            activated = false;
        }
        //System.out.println("VOID DEActivated for ("+fsRootPath+"):"+fileSystem);
    }
    
    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport != null) {
            //System.out.println(this+"addPropertyChangeListener("+l+")");
            propertyChangeSupport.addPropertyChangeListener(l);
        }
    }
    
    public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport != null) {
            //System.out.println(this+"removePropertyChangeListener("+l+")");
            propertyChangeSupport.removePropertyChangeListener(l);
        }
    }
    
    private void firePropertyChange() {
        //System.out.println("VcsObjectIntegritySupport.firePropertyChange("+(propertyChangeSupport != null)+")");
        PropertyChangeSupport propertyChangeSupport;
        synchronized (this) {
            propertyChangeSupport = this.propertyChangeSupport;
        }
        if (propertyChangeSupport != null) {
            //System.out.println("listeners = "+java.util.Arrays.asList(propertyChangeSupport.getPropertyChangeListeners()));
            propertyChangeSupport.firePropertyChange(PROPERTY_FILES_CHANGED, null, null);
        }
    }
    
    /**
     * Called after a DataObject is recognized. Do not call this directly!
     */
    public void operationPostCreate(OperationEvent operationEvent) {
        DataObject dobj = operationEvent.getObject();
        //System.out.println("operationPostCreate("+dobj+")");
        synchronized (objectsToAnalyze) {
            objectsToAnalyze.add(dobj);
        }
        analyzerTask.schedule(ANALYZER_SCHEDULE_TIME);
    }
    
    /**
     * Get the created DataObject from the queue, analyze their files and
     * add the file names into the integrity list if necessary.
     */
    public void run() {
        Set paths;
        Set objects;
        boolean changed = false;
        synchronized (objectsToAnalyze) {
            paths = pathsToAnalyze;
            objects = objectsToAnalyze;
            pathsToAnalyze = new HashSet();
            objectsToAnalyze = new HashSet();
        }
        if (!paths.isEmpty()) {
            Enumeration existingEnum = foExistence.getExistingFiles();
            while(existingEnum.hasMoreElements() && !paths.isEmpty()) {
                FileObject fo = (FileObject) existingEnum.nextElement();
                if (paths.remove(fo.getPath())) {
                    try {
                        DataObject dobj = DataObject.find(fo);
                        objects.add(dobj);
                    } catch (DataObjectNotFoundException donfex) {
                        // Ignored. If the DO does not exist, it can not be analyzed.
                    }
                }
            }
        }
        for (Iterator objIt = objects.iterator(); objIt.hasNext(); ) {
            DataObject dobj = (DataObject) objIt.next();
            FileObject primary = dobj.getPrimaryFile();
            FileSystem fs = (FileSystem) primary.getAttribute(VcsAttributes.VCS_NATIVE_FS);
            if (primary.isFolder() || !fileSystem.equals(fs)) {
                //System.out.println("VOIS.run(): ignoring primary = "+primary+" from "+fs);
                continue;
            }
            //fileSystem.getCacheProvider().
            File primaryFile = FileUtil.toFile(primary);
            CacheFile pcFile = cache.getCacheFile(primaryFile, CacheHandler.STRAT_DISK, null);
            if (pcFile == null || pcFile.isLocal()) {
                synchronized (primaryLocalFiles) {
                    primaryLocalFiles.add(primary.getPath());
                }
                changed = true;
                continue;
            } else {
                synchronized (primaryLocalFiles) {
                    if (primaryLocalFiles.remove(primary.getPath())) {
                        changed = true;
                    }
                }
            }
            String primaryFilePath = primary.getPath();
            //System.out.println("VOIS.run(): have primary: "+primaryFilePath);
            dobj.addPropertyChangeListener(doFileChangeListener);
            Set filesToAdd = new HashSet();
            Set filesToRemove = new HashSet();
            Set fileSet = dobj.files();
            for (Iterator fileIt = fileSet.iterator(); fileIt.hasNext(); ) {
                FileObject fo = (FileObject) fileIt.next();
                if (primary.equals(fo)) continue;
                String filePath = fo.getPath();
                fs = (FileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
                if (fo.isFolder() || !fileSystem.equals(fs) ||
                    !foImportantness.isImportant(filePath) ||
                    ignoredSecondaryLocalFiles.contains(filePath)) {
                        
                    filesToRemove.add(filePath);
                    continue;
                }
                File file = FileUtil.toFile(fo);
                CacheFile cFile = cache.getCacheFile(file, CacheHandler.STRAT_DISK, null);
                //System.out.println("   VOIS.run(): secondary '"+fo+"', cache = "+cFile);
                if (cFile == null || cFile.isLocal()) {
                    filesToAdd.add(filePath);
                } else {
                    filesToRemove.add(filePath);
                }
            }
            synchronized (objectsWithLocalFiles) {
                Set localSec = (Set) objectsWithLocalFiles.get(primaryFilePath);
                if (localSec == null) {
                    localSec = new HashSet();
                }
                localSec.addAll(filesToAdd);
                for (Iterator fileIt = filesToAdd.iterator(); fileIt.hasNext(); ) {
                    String secFile = (String) fileIt.next();
                    filesMap.put(secFile, primaryFilePath);
                }
                localSec.removeAll(filesToRemove);
                filesMap.keySet().removeAll(filesToRemove);
                if (localSec.size() > 0) {
                    objectsWithLocalFiles.put(primaryFilePath, localSec);
                    changed = true;
                } else {
                    Object removedObj = objectsWithLocalFiles.remove(primaryFilePath);
                    if (removedObj != null) changed = true;
                }
            }
        }
        if (changed) firePropertyChange();
    }
    
    /**
     * Return the map of primary file names with a set of local secondary files.
     */
    public Map getObjectsWithLocalFiles() {
        synchronized (objectsWithLocalFiles) {
            return Collections.unmodifiableMap(new HashMap(objectsWithLocalFiles));
        }
    }
    
    public void addIgnoredFiles(String[] ignoredFilePaths) {
        ignoredSecondaryLocalFiles.addAll(Arrays.asList(ignoredFilePaths));
        //System.out.println("ignoredSecondaryLocalFiles = "+ignoredSecondaryLocalFiles);
        boolean changed = false;
        synchronized (objectsWithLocalFiles) {
            for (int i = 0; i < ignoredFilePaths.length; i++) {
                String primary = (String) filesMap.remove(ignoredFilePaths[i]);
                if (primary != null) {
                    // It was a local secondary file, so it needs to be removed.
                    Set localSec = (Set) objectsWithLocalFiles.get(primary);
                    if (localSec != null) {
                        //System.out.println("    removing secondary: "+path);
                        localSec.remove(ignoredFilePaths[i]);
                        if (localSec.size() == 0) {
                            objectsWithLocalFiles.remove(primary);
                        }
                        changed = true;
                    }
                }
            }
        }
        if (changed) firePropertyChange();
    }
    
    /**
     * Find a FileObject for the given file path on the associated filesystem.
     */
    public FileObject findFileObject(String name) {
        return fileSystem.findResource(name);
    }
    
    private static final String getFilePath(CacheFile cFile, String fsRootPath) {
        String absPath = cFile.getAbsolutePath();
        String path = null;
        if (absPath.startsWith(fsRootPath)) {
            if (fsRootPath.length() == absPath.length()) {
                path = "";
            } else {
                path = absPath.substring(fsRootPath.length() + 1, absPath.length());
            }
            path = path.replace(File.separatorChar, '/');
         }
        return path;
    }
    
    /** is called when a file/dir is added to the cache. The filesystem should
     * generally perform findResource() on the dir the added files is in
     * and do refresh of that directory.
     */
    public void cacheAdded(CacheHandlerEvent event) {
        CacheFile cFile = event.getCacheFile();
        //System.out.println("VOIS.cacheAdded("+cFile+")");
        if (cFile instanceof CacheDir || cFile.isLocal()) return ;
        String path = getFilePath(cFile, fsRootPath);
        if (path != null) {
            boolean wasLocal;
            synchronized (primaryLocalFiles) {
                wasLocal = primaryLocalFiles.remove(path);
            }
            if (wasLocal) {
                // It was a local primary file; now it's not local, we need to
                // analyze the DataObject again.
                synchronized (objectsToAnalyze) {
                    pathsToAnalyze.add(path);
                }
                analyzerTask.schedule(ANALYZER_SCHEDULE_TIME);
                return ;
            }
            boolean changed = false;
            synchronized (objectsWithLocalFiles) {
                String primary = (String) filesMap.remove(path);
                if (primary != null) {
                    // It was a local secondary file, so it needs to be removed.
                    Set localSec = (Set) objectsWithLocalFiles.get(primary);
                    if (localSec != null) {
                        //System.out.println("    removing secondary: "+path);
                        localSec.remove(path);
                        if (localSec.size() == 0) {
                            objectsWithLocalFiles.remove(primary);
                        }
                        changed = true;
                    }
                }
            }
            if (changed) firePropertyChange();
        }
    }
    
    /** is Called when a file/dir is removed from cache.
     */
    public void cacheRemoved(CacheHandlerEvent event) {
        CacheFile cFile = event.getCacheFile();
        // If the removed cache file was local, ignore it, because only delete
        // can remove it entirely.
        if (cFile instanceof CacheDir || cFile.isLocal()) return ;
        String path = getFilePath(cFile, fsRootPath);
        if (path != null) {
            boolean changed = false;
            synchronized (objectsWithLocalFiles) {
                // If it was a primary file, remove all local secondary files:
                Set localSet = (Set) objectsWithLocalFiles.remove(path);
                if (localSet != null) {
                    for (Iterator it = localSet.iterator(); it.hasNext(); ) {
                        String secondary = (String) it.next();
                        filesMap.remove(secondary);
                    }
                    changed = true;
                }
            }
            if (changed) firePropertyChange();
        }
    }
    
    /** is called each time the status of a file changes in cache.
     * The filesystem has to decide wheater it affects him (only in case when
     * there's not the 1-to-1 relationship between cache and fs.
     */
    public void statusChanged(CacheHandlerEvent event) {
        CacheFile cFile = event.getCacheFile();
        if (cFile instanceof CacheDir) return ;
        //System.out.println("VOIS.statusChanged("+cFile+")");
        String path = getFilePath(cFile, fsRootPath);
        if (path != null) {
            boolean changed = false;
            if (cFile.isLocal()) {
                synchronized (objectsWithLocalFiles) {
                    // If it was a primary file, remove all local secondary files:
                    Set localSet = (Set) objectsWithLocalFiles.remove(path);
                    if (localSet != null) {
                        //System.out.println("   Removed primary: "+path);
                        for (Iterator it = localSet.iterator(); it.hasNext(); ) {
                            String secondary = (String) it.next();
                            filesMap.remove(secondary);
                        }
                        synchronized (primaryLocalFiles) {
                            primaryLocalFiles.add(path);
                        }
                        changed = true;
                    }
                    // If a versioned secondary FileObject becomes local,
                    // it can be too performance expensive to find the
                    // appropriate primary file. It is supposed, that this
                    // does occur very rearly.
                }
            } else {
                synchronized (objectsWithLocalFiles) {
                    // If a secondary FileObject becomes non-local, remove it
                    // from the maps.
                    String primary = (String) filesMap.remove(path);
                    if (primary != null) {
                        Set localSet = (Set) objectsWithLocalFiles.get(primary);
                        if (localSet != null) {
                            localSet.remove(path);
                        }
                        changed = true;
                    }
                }
                synchronized (primaryLocalFiles) {
                    // If a primary file becomes non-local, an analysis must be made
                    if (primaryLocalFiles.remove(path)) {
                        // It was a local primary file; now it's not local, we need to
                        // analyze the DataObject again.
                        synchronized (objectsToAnalyze) {
                            pathsToAnalyze.add(path);
                        }
                        analyzerTask.schedule(ANALYZER_SCHEDULE_TIME);
                    }
                }
            }
            if (changed) firePropertyChange();
        }
    }
    
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        if (ignoredSecondaryLocalFiles == null) {
            ignoredSecondaryLocalFiles = Collections.synchronizedSet(new HashSet());
        }
    }
    
    /**
     * Provides the String representation. Mainly for debug purposes.
     *
    public String toString() {
        StringBuffer localSecondaryFiles = new StringBuffer();
        int numLocSec;
        StringBuffer primaryLocal = new StringBuffer();
        int numPrimLoc;
        StringBuffer ignored = new StringBuffer();
        synchronized (objectsWithLocalFiles) {
            for (Iterator it = filesMap.keySet().iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                localSecondaryFiles.append("\t");
                localSecondaryFiles.append(name);
                if (it.hasNext()) localSecondaryFiles.append(",\n");
            }
            numLocSec = filesMap.size();
            synchronized (primaryLocalFiles) {
                for (Iterator it = primaryLocalFiles.iterator(); it.hasNext(); ) {
                    String name = (String) it.next();
                    primaryLocal.append("\t");
                    primaryLocal.append(name);
                    if (it.hasNext()) primaryLocal.append(",\n");
                }
                numPrimLoc = primaryLocalFiles.size();
            }
        }
        int numIgnored;
        synchronized (ignoredSecondaryLocalFiles) {
            for (Iterator it = ignoredSecondaryLocalFiles.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                ignored.append("\t");
                ignored.append(name);
                if (it.hasNext()) ignored.append(",\n");
            }
            numIgnored = ignoredSecondaryLocalFiles.size();
        }
        return "VcsObjectIntegritySupport "+Integer.toHexString(hashCode())+"\n"+
            "  Local Secondary Files: "+numLocSec+"\n"+
            ((localSecondaryFiles.length() > 0) ? localSecondaryFiles.toString()+"\n" : "")+
            "  Local Primary Files: "+numPrimLoc+"\n"+
            ((primaryLocal.length() > 0) ? primaryLocal.toString()+"\n" : "")+
            "  Ignored Local Secondary Files: "+numIgnored+"\n"+
            ((ignored.length() > 0) ? ignored.toString()+"\n" : "");
    }
     */
    
    
    /**
     * It's necessary to catch the changes of files in versioned DataObjects.
     */
    private class DOFileChangeListener extends Object implements PropertyChangeListener {
        
        /** This method gets called when a bound property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *   	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (DataObject.PROP_FILES.equals(name)) {
                DataObject dobj = (DataObject) evt.getSource();
                //System.out.println("FILES Change of "+dobj);
                synchronized (objectsToAnalyze) {
                    objectsToAnalyze.add(dobj);
                }
                analyzerTask.schedule(ANALYZER_SCHEDULE_TIME);
            }
        }
        
    }
    
    // Utility static stuff:
    
    /**
     * Returns a map of providers and the associated files. The associated
     * files are an array of FileObjects.
     */
    private static Map findCommandProvidersForFiles(FileObject[] files) {
        Map providers = new HashMap();
        for (int i = 0; i < files.length; i++) {
            FileObject fo = files[i];
            VcsCommandsProvider provider = VcsCommandsProvider.findProvider(fo);
            //System.out.println("  fo = "+fo+" provider = "+provider);
            if (provider != null) {
                if (providers.containsKey(provider)) {
                    List fileList = (List) providers.get(provider);
                    fileList.add(fo);
                } else {
                    List fileList = new ArrayList();
                    fileList.add(fo);
                    providers.put(provider, fileList);
                    //System.out.println("  put("+provider+", "+fileList+")");
                }
            }
        }
        for (Iterator it = providers.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            List fileList = (List) providers.get(provider);
            FileObject[] fileArray = (FileObject[]) fileList.toArray(new FileObject[fileList.size()]);
            providers.put(provider, fileArray);
        }
        return providers;
    }
    
    /**
     * Run the add command of files, that are necessary to add to keep the integrity
     * of objects.
     * @param files The list of files, that are to be integrated into VCS.
     * @param origCommand The original (CheckInCommand) command, that is used
     *        just to copy some options from (to retain the expert and GUI mode).
     * @return true when the integrity keeper was customized successfully, or
     *         false when it was canceled and so the commit should be canceled
     *         as well.
     */
    public static boolean runIntegrityKeeper(FileObject[] files, Command origCommand) {
        //System.out.println("VOIS.runIntegrityKeeper("+java.util.Arrays.asList(files)+", "+origCommand+")");
        Map providersForFiles = findCommandProvidersForFiles(files);
        boolean customized = true;
        for (Iterator it = providersForFiles.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            AddCommand addCmd = (AddCommand) provider.createCommand(AddCommand.class);
            //System.out.println("  "+provider+".createCommand(AddCommand.class) = "+addCmd);
            if (addCmd != null) {
                FileObject[] cmdFiles = (FileObject[]) providersForFiles.get(provider);
                //System.out.println("  cmdFiles = "+java.util.Arrays.asList(cmdFiles));
                VcsObjectIntegritySupport objectIntegritySupport = null;
                FileSystem fs = (FileSystem) cmdFiles[0].getAttribute(VcsAttributes.VCS_NATIVE_FS);
                if (fs != null) {
                    objectIntegritySupport = IntegritySupportMaintainer.findObjectIntegritySupport(fs);
                }
                //System.out.println("  objectIntegritySupport = "+objectIntegritySupport);
                if (objectIntegritySupport == null) continue;
                ObjectIntegrityCommandSupport integrityCmdSupport = new ObjectIntegrityCommandSupport();
                ObjectIntegrityCommand integrityCmd = 
                    (ObjectIntegrityCommand) integrityCmdSupport.createCommand();
                integrityCmd.setAddCommand(addCmd);
                integrityCmd.setObjectIntegritySupport(objectIntegritySupport);
                integrityCmd.setFiles(cmdFiles);
                integrityCmd.setExpertMode(origCommand.isExpertMode());
                integrityCmd.setGUIMode(origCommand.isGUIMode());
                customized = VcsManager.getDefault().showCustomizer(integrityCmd);
                if (customized) {
                    CommandTask task = integrityCmd.execute();
                    try {
                        task.waitFinished(0);
                    } catch (InterruptedException iex) {
                        // Ignore and stop waiting.
                    }
                }
            }
        }
        return customized;
    }
    
}
