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

package org.netbeans.modules.vcscore.registry;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openide.util.SharedClassObject;

import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;

/**
 * The container of manually recognized and unrecognized VCS filesystems.
 *
 * @author  Martin Entlicher
 */
public class RecognizedFS extends Object implements Serializable {
    
    private Set manuallyRecognized;
    private Set removedRoots;
    
    private static RecognizedFS defaultInstance;
    
    static final long serialVersionUID = -2248315346064169290L;
    
    /** Creates a new instance of RecognizedFS */
    private RecognizedFS() {
        manuallyRecognized = new HashSet();
        removedRoots = new HashSet();
    }
    
    public static synchronized RecognizedFS getDefault() {
        if (defaultInstance == null) {
        //faw    GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        //faw    defaultInstance = settings.getRecognizedFS();
            if (defaultInstance == null) {
                defaultInstance = new RecognizedFS();
            }
        }
        return defaultInstance;
    }
    
    private void saveMe() {
       //faw  GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
       //faw settings.setRecognizedFS((RecognizedFS) this.clone()); // To assure that an asynchronous save will not encounter modified sets by other threads
    }
    
    /**
     * Get the set of manually recognized VCS filesystems.
     * @return The set of FSInfo objects.
     */
    public synchronized Set getManuallyRecognized() {
        return Collections.unmodifiableSet(manuallyRecognized);
    }
    
    /**
     * Find out, whether the given filesystem info was recognized manually or not.
     */
    public synchronized boolean isManuallyRecognized(FSInfo fsInfo) {
        return manuallyRecognized.contains(fsInfo);
    }
    
    /**
     * Add a new manually recognized VCS filesystem.
     */
    public synchronized void addManuallyRecognized(FSInfo fsInfo) {
        manuallyRecognized.add(fsInfo);
        saveMe();
    }

    /**
     * Remove a recognized VCS filesystem.
     */
    public synchronized void removeRecognized(FSInfo fsInfo) {
        manuallyRecognized.remove(fsInfo);
        removedRoots.add(fsInfo.getFSRoot());
        saveMe();
    }
    
    /**
     * Test, whether some kind of recognized filesystem was removed from the
     * specified root path.
     */
    public synchronized boolean isRecognitionRemoved(File root) {
        return removedRoots.contains(root);
    }
    
    public synchronized Object clone() {
        RecognizedFS clon = new RecognizedFS();
        clon.manuallyRecognized = new HashSet(this.manuallyRecognized);
        clon.removedRoots = new HashSet(this.removedRoots);
        return clon;
    }
    
}
