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
package org.netbeans.modules.mdr;

import org.netbeans.api.mdr.*;
import org.netbeans.mdr.NBMDRepositoryImpl;
import org.openide.util.Lookup;
import org.openide.filesystems.*;
import org.openide.loaders.*;

import org.netbeans.mdr.handlers.BaseObjectHandler;
import org.netbeans.mdr.handlers.ClassLoaderProvider;

import java.util.*;
import org.openide.ErrorManager;

/**
 *
 * @author Martin Matula
 * @version 
 */
public class MDRManagerImpl extends MDRManager implements FileChangeListener, NBMDRepositoryImpl.ShutdownListener {
    
    private static final String FOLDER_REPOSITORY = "MDRepositories";
    private static final String DEFAULT_REPOSITORY = "Default";
    
    private HashMap repositoryMap = null;
    private FileObject repFolder = null;
    private static MDRManagerImpl instance = null;
    
    static {
        BaseObjectHandler.setClassLoaderProvider(new CLProviderImpl());
    }
    
    static synchronized void uninstall() {
        if (instance != null) {
            instance.uninstallMe();
        }
    }
    
    private void uninstallMe() {
        repositoryMap = null;
        if (repFolder != null) {
            repFolder.removeFileChangeListener(this);
        }
        repFolder = null;
    }

    private void refreshChildren() {
        if (repFolder == null) {
            repFolder = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(FOLDER_REPOSITORY);
            repFolder.addFileChangeListener(this);
        }
        
        repositoryMap.clear();
        
        if (repFolder != null) {
            for (Enumeration enum = repFolder.getData(false); enum.hasMoreElements();) {
                try {
                    DataObject dataObject = DataObject.find((FileObject) enum.nextElement());
                    if (dataObject instanceof MDRDataObject) {
                        repositoryMap.put(dataObject.getName(), dataObject);
                        ErrorManager.getDefault().log("found repository: " + dataObject.getName());
                    }
                } catch (DataObjectNotFoundException e) {
                    // ignore
                }
            }
        }
    }
    
    private void init() {
        if (repositoryMap == null) {
            repositoryMap = new HashMap();
            refreshChildren();
        }
    }
    
    /** Creates new MDRManagerImpl */
    public MDRManagerImpl() {
        instance = this;
    }

    public void shutdownAll() {
        String names[] = getRepositoryNames();
        MDRepository[] repos = new MDRepository[names.length];
        int stepCount = 0;
        for (int i = 0; i < names.length; i++) {
            repos[i] = getRepository(names[i]);
            if (repos[i] instanceof NBMDRepositoryImpl) {
                stepCount += ((NBMDRepositoryImpl) repos[i]).getShutdownSteps();
            } else {
                stepCount++;
            }
        }
        fireProgressListenerStart(stepCount);
        for (int i = 0; i < names.length; i++) {
            ((NBMDRepositoryImpl) repos[i]).addShutdownListener(this);
            repos[i].shutdown();
            ((NBMDRepositoryImpl) repos[i]).removeShutdownListener(this);
        }
        fireProgressListenerStop();
    }
    
    // === ShutdownListener implementation ===
    
    public void shutdown() {
    }
    
    public void stepFinished() {
        fireProgressListenerStep();
    } // === end of ShutdownListener implementation ===
    
    //=== Progress suppport ===
    
    private ShutDownProgressListener progress = null;
    private boolean stopFired = false;
    
    boolean setProgressListener(ShutDownProgressListener pl) {
        boolean shutdownListenerRegistered = false;
        if (progress == null) {
            progress = pl;
            shutdownListenerRegistered = true;
        }
        return shutdownListenerRegistered && !stopFired;
    }
    
    private void fireProgressListenerStart(int count) {
        if (progress == null)
            return;
        progress.start(count);
    }
        
    public void fireProgressListenerStep() {
        if (progress == null)
            return;
        progress.step();
    }
    
    private void fireProgressListenerStop() {
        if (progress == null) {
            stopFired = true;
            return;
        }
        progress.stop();
    }
    //== end of Progress Support ==

    
    public MDRepository getRepository(String name) {
        init();
        MDRDataObject dataObject = (MDRDataObject) repositoryMap.get(name);
        if (dataObject == null) {
            return null;
        } else {
            MDRDescriptor repository = dataObject.getDescriptor();
            return repository.getMDRInstance();
        }
    }
    
    public MDRepository getDefaultRepository() {
        return getRepository(DEFAULT_REPOSITORY);
    }

    public String[] getRepositoryNames() {
        init();
        String[] result = new String[0];
        result = (String[]) repositoryMap.keySet().toArray(result);
        return result;
    }
    
    /** Fired when a new folder is created. This action can only be
     * listened to in folders containing the created folder up to the root of
     * file system.
     *
     * @param fe the event describing context where action has taken place
     */
    public void fileFolderCreated(FileEvent fe) {
        refreshChildren();
    }
    
    /** Fired when a new file is created. This action can only be
     * listened in folders containing the created file up to the root of
     * file system.
     *
     * @param fe the event describing context where action has taken place
     */
    public void fileDataCreated(FileEvent fe) {
        refreshChildren();
    }
    
    /** Fired when a file is changed.
     * @param fe the event describing context where action has taken place
     */
    public void fileChanged(FileEvent fe) {
        refreshChildren();
    }
    
    /** Fired when a file is deleted.
     * @param fe the event describing context where action has taken place
     */
    public void fileDeleted(FileEvent fe) {
        refreshChildren();
    }
    
    /** Fired when a file is renamed.
     * @param fe the event describing context where action has taken place
     *          and the original name and extension.
     */
    public void fileRenamed(FileRenameEvent fe) {
        refreshChildren();
    }
    
    /** Fired when a file attribute is changed.
     * @param fe the event describing context where action has taken place,
     *          the name of attribute and the old and new values.
     */
    public void fileAttributeChanged(FileAttributeEvent fe) {
        refreshChildren();
    }
    
    private static class CLProviderImpl implements ClassLoaderProvider {
        public ClassLoader getClassLoader() {
            return (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
        }
        
        /** Implementation of this method can define a given class.
         * The defined class should be then visible from the ClassLoader returned
         * from {@link #getClassLoader} method.
         * @param className name of the class to define.
         * @param classFile
         * @return Defined class or null (if null is return, MDR will define the
         * class in its own classloader. This class will then not be accessible from outside
         * of MDR
         */
        public Class defineClass(String className, byte[] classFile) {
            return null;
        }
    }
}
