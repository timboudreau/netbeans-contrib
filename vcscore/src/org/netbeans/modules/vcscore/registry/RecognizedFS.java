/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.registry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

import org.openide.util.SharedClassObject;
import org.openide.util.WeakListeners;

import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;

/**
 * The container of manually recognized and unrecognized VCS filesystems.
 *
 * @author  Martin Entlicher
 */
public class RecognizedFS extends Object implements Serializable, PropertyChangeListener {
    
    private static RecognizedFS defaultInstance;
    
    private Set manuallyRecognized;
    private Set removedRoots;
    private transient PropertyChangeSupport pchs = new PropertyChangeSupport(this);
    
    static final long serialVersionUID = -2248315346064169290L;
    
    /** Creates a new instance of RecognizedFS */
    private RecognizedFS() {
        manuallyRecognized = new HashSet();
        removedRoots = new HashSet();
    }
    
    private static boolean readFromSettings = false;
    
    public static RecognizedFS getDefault() {
        synchronized (RecognizedFS.class) {
            if (defaultInstance != null) return defaultInstance;
        }
        if (readFromSettings) {
            getDefaultFromSettings();
        } else {
            final Lookup.Result r = Lookup.getDefault().lookup(new Lookup.Template(GeneralVcsSettings.class));
            r.allInstances().size(); // This initializes the looked-up class outside of a lock from SharedClassObject.findObject()
            if (readFromSettings) { // It was actually read
                getDefaultFromSettings();
            } else { // Create an empty object and hope that it will be read later...
                synchronized (RecognizedFS.class) {
                    if (defaultInstance == null) {
                        defaultInstance = new RecognizedFS();
                    }
                }
            }
        }
        return defaultInstance;
    }
    
    private static void getDefaultFromSettings() {
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        boolean fire = false;
        RecognizedFS settingsFS;
        synchronized (RecognizedFS.class) {
            settingsFS = settings.getRecognizedFS();
            if (defaultInstance == null) {
                defaultInstance = settingsFS;
                if (defaultInstance == null) {
                    defaultInstance = new RecognizedFS();
                }
            } else {
                if (settingsFS != null) {
                    // The other pass already initialized the defaultInstance,
                    // we need to merge it with the settings...
                    defaultInstance.manuallyRecognized.addAll(settingsFS.manuallyRecognized);
                    defaultInstance.removedRoots.addAll(settingsFS.removedRoots);
                    fire = true;
                }
            }
            if (settingsFS != null) { // Attach the listener to the FSInfo from settings only.
                for (Iterator it = settingsFS.manuallyRecognized.iterator(); it.hasNext(); ) {
                    FSInfo fsInfo = (FSInfo) it.next();
                    fsInfo.addPropertyChangeListener(WeakListeners.propertyChange(defaultInstance, fsInfo));
                }
            }
        }
        if (fire) {
            for (Iterator it = settingsFS.manuallyRecognized.iterator(); it.hasNext(); ) {
                defaultInstance.firePropertyChange("manuallyRecognized", it.next());
            }
        }
    }
    
    private void saveMe() {
       GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
       settings.setRecognizedFS((RecognizedFS) this.clone()); // To assure that an asynchronous save will not encounter modified sets by other threads
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
        fsInfo.addPropertyChangeListener(WeakListeners.propertyChange(this, fsInfo));
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
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        saveMe();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pchs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pchs.removePropertyChangeListener(l);
    }
    
    private void firePropertyChange(String propertyName, Object fsInfoValue) {
        pchs.firePropertyChange(new PropertyChangeEvent(this, propertyName, null, fsInfoValue));
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        pchs = new PropertyChangeSupport(this);
        boolean hasDefaultInstance;
        synchronized (RecognizedFS.class) {
            readFromSettings = true;
            hasDefaultInstance = defaultInstance != null;
        }
        if (hasDefaultInstance) { // Update the default instance from settings
            final GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
            settings.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName() == null) { // Loading finished
                        getDefaultFromSettings();
                        settings.removePropertyChangeListener(this);
                    }
                }
            });
        }
    }
    
}
