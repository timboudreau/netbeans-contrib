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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.Command;

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
    
    private static final int ANALYZER_SCHEDULE_TIME = 200;
    
    private transient FileSystem fileSystem;
    private transient FileSystemCache cache;
    private transient String fsRootPath;
    private transient FileObjectImportantness foImportantness;
    private transient Set objectsToAnalyze;
    private transient RequestProcessor.Task analyzerTask;
    private transient boolean activated = false;
    
    //private List localFileNames = new ArrayList();
    /** A map of set of names of local secondary files by names of primary files. */
    private Map objectsWithLocalFiles = new HashMap();
    /** A map of primary file names by local secondary files. */
    private Map filesMap = new HashMap();
    /** The set of names of primary files, that are local. They might become non-local later. */
    private Set primaryLocalFiles = new HashSet();
    
    /** Creates a new instance of VcsObjectIntegritySupport.
     * It's unusable until it's activated.
     */
    public VcsObjectIntegritySupport() {
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
                                      FileObjectImportantness foImportantness) {
        this.fileSystem = fileSystem;
        this.cache = cache;
        this.fsRootPath = fsRootPath;
        this.foImportantness = foImportantness;
        this.objectsToAnalyze = new HashSet();
        this.analyzerTask = RequestProcessor.getDefault().post(this, ANALYZER_SCHEDULE_TIME, Thread.MIN_PRIORITY);
        DataLoaderPool pool = (DataLoaderPool) Lookup.getDefault().lookup(DataLoaderPool.class);
        pool.addOperationListener((OperationListener) WeakListener.create(OperationListener.class, this, pool));
        cache.addCacheHandlerListener((CacheHandlerListener) WeakListener.create(CacheHandlerListener.class, this, cache));
        this.activated = true;
    }
    
    /**
     * Called after a DataObject is recognized. Do not call this directly!
     */
    public void operationPostCreate(OperationEvent operationEvent) {
        //System.out.println("operationPostCreate("+operationEvent+")");
        DataObject dobj = operationEvent.getObject();
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
        Set objects;
        synchronized (objectsToAnalyze) {
            objects = objectsToAnalyze;
            objectsToAnalyze = new HashSet();
        }
        for (Iterator objIt = objects.iterator(); objIt.hasNext(); ) {
            DataObject dobj = (DataObject) objIt.next();
            FileObject primary = dobj.getPrimaryFile();
            FileSystem fs = (FileSystem) primary.getAttribute(VcsAttributes.VCS_NATIVE_FS);
            if (primary.isFolder() || !fileSystem.equals(fs)) {
                continue;
            }
            //fileSystem.getCacheProvider().
            File primaryFile = FileUtil.toFile(primary);
            CacheFile pcFile = cache.getCacheFile(primaryFile, CacheHandler.STRAT_DISK, null);
            if (pcFile == null || pcFile.isLocal()) {
                synchronized (primaryLocalFiles) {
                    primaryLocalFiles.add(primary.getPath());
                }
                continue;
            } else {
                synchronized (primaryLocalFiles) {
                    primaryLocalFiles.remove(primary.getPath());
                }
            }
            String primaryFilePath = primary.getPath();
            synchronized (objectsWithLocalFiles) {
                Set localSec = (Set) objectsWithLocalFiles.get(primaryFilePath);
                if (localSec == null) {
                    localSec = new HashSet();
                }
                Set fileSet = dobj.files();
                for (Iterator fileIt = fileSet.iterator(); fileIt.hasNext(); ) {
                    FileObject fo = (FileObject) fileIt.next();
                    String filePath = fo.getPath();
                    String primaryForThis = (String) filesMap.get(filePath);
                    if (primaryForThis != null && !primaryFilePath.equals(primaryForThis)) {
                        filesMap.remove(filePath);
                    }
                    fs = (FileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
                    if (fo.isFolder() || !fileSystem.equals(fs) ||
                        !foImportantness.isImportant(filePath)) {
                        
                        filesMap.remove(filePath);
                        localSec.remove(filePath);
                        continue;
                    }
                    File file = FileUtil.toFile(fo);
                    CacheFile cFile = cache.getCacheFile(file, CacheHandler.STRAT_DISK, null);
                    if (cFile == null || cFile.isLocal()) {
                        localSec.add(filePath);
                        filesMap.put(filePath, primaryFilePath);
                    } else {
                        filesMap.remove(filePath);
                        localSec.remove(filePath);
                    }
                }
                if (localSec.size() > 0) {
                    objectsWithLocalFiles.put(primaryFilePath, localSec);
                } else {
                    objectsWithLocalFiles.remove(primaryFilePath);
                }
            }
        }
    }
    
    /**
     * Return the map of primary file names with a set of local secondary files.
     */
    public Map getObjectsWithLocalFiles() {
        synchronized (objectsWithLocalFiles) {
            return Collections.unmodifiableMap(new HashMap(objectsWithLocalFiles));
        }
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
                FileObject fo = fileSystem.findResource(path);
                if (fo != null) {
                    try {
                        DataObject dobj = DataObject.find(fo);
                        synchronized (objectsToAnalyze) {
                            objectsToAnalyze.add(dobj);
                        }
                        analyzerTask.schedule(ANALYZER_SCHEDULE_TIME);
                    } catch (DataObjectNotFoundException donfex) {}
                }
                return ;
            }
            synchronized (objectsWithLocalFiles) {
                String primary = (String) filesMap.remove(path);
                if (primary != null) {
                    // It was a local secondary file, so it needs to be removed.
                    Set localSec = (Set) objectsWithLocalFiles.get(primary);
                    if (localSec != null) {
                        localSec.remove(path);
                        if (localSec.size() == 0) {
                            objectsWithLocalFiles.remove(primary);
                        }
                    }
                }
            }
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
            synchronized (objectsWithLocalFiles) {
                // If it was a primary file, remove all local secondary files:
                Set localSet = (Set) objectsWithLocalFiles.remove(path);
                if (localSet != null) {
                    for (Iterator it = localSet.iterator(); it.hasNext(); ) {
                        String secondary = (String) it.next();
                        filesMap.remove(secondary);
                    }
                }
            }
        }
    }
    
    /** is called each time the status of a file changes in cache.
     * The filesystem has to decide wheater it affects him (only in case when
     * there's not the 1-to-1 relationship between cache and fs.
     */
    public void statusChanged(CacheHandlerEvent event) {
        CacheFile cFile = event.getCacheFile();
        if (cFile instanceof CacheDir) return ;
        String path = getFilePath(cFile, fsRootPath);
        if (path != null) {
            if (cFile.isLocal()) {
                synchronized (objectsWithLocalFiles) {
                    // If it was a primary file, remove all local secondary files:
                    Set localSet = (Set) objectsWithLocalFiles.remove(path);
                    if (localSet != null) {
                        for (Iterator it = localSet.iterator(); it.hasNext(); ) {
                            String secondary = (String) it.next();
                            filesMap.remove(secondary);
                        }
                        synchronized (primaryLocalFiles) {
                            primaryLocalFiles.add(path);
                        }
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
                    }
                }
                synchronized (primaryLocalFiles) {
                    // If a primary file becomes non-local, an analysis must be made
                    if (primaryLocalFiles.remove(path)) {
                        // It was a local primary file; now it's not local, we need to
                        // analyze the DataObject again.
                        FileObject fo = fileSystem.findResource(path);
                        if (fo != null) {
                            try {
                                DataObject dobj = DataObject.find(fo);
                                synchronized (objectsToAnalyze) {
                                    objectsToAnalyze.add(dobj);
                                }
                                analyzerTask.schedule(ANALYZER_SCHEDULE_TIME);
                            } catch (DataObjectNotFoundException donfex) {}
                        }
                    }
                }
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
     */
    public static void runIntegrityKeeper(FileObject[] files, Command origCommand) {
        Map providersForFiles = findCommandProvidersForFiles(files);
        for (Iterator it = providersForFiles.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            AddCommand addCmd = (AddCommand) provider.createCommand(AddCommand.class);
            if (addCmd != null) {
                FileObject[] cmdFiles = (FileObject[]) providersForFiles.get(provider);
                VcsObjectIntegritySupport objectIntegritySupport =
                    (VcsObjectIntegritySupport) cmdFiles[0].getAttribute(VcsObjectIntegritySupport.ATTRIBUTE_NAME);
                if (objectIntegritySupport == null) continue;
                ObjectIntegrityCommandSupport integrityCmdSupport = new ObjectIntegrityCommandSupport();
                ObjectIntegrityCommand integrityCmd = 
                    (ObjectIntegrityCommand) integrityCmdSupport.createCommand();
                integrityCmd.setAddCommand(addCmd);
                integrityCmd.setObjectIntegritySupport(objectIntegritySupport);
                integrityCmd.setFiles(cmdFiles);
                integrityCmd.setExpertMode(origCommand.isExpertMode());
                integrityCmd.setGUIMode(origCommand.isGUIMode());
                boolean customized = VcsManager.getDefault().showCustomizer(integrityCmd);
                if (customized) integrityCmd.execute();
            }
        }
    }
    
}
