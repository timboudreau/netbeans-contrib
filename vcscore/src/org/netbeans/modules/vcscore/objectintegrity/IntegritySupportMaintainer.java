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
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Map;
import java.util.WeakHashMap;

import org.openide.filesystems.FileSystem;
import org.openide.util.WeakListener;

/**
 * The maintainer of VcsObjectIntegritySupport objects. This service takes care
 * that when the root of the FS changes, the old VOIS (VcsObjectIntegritySupport)
 * is saved and the new is read from attributes.
 *
 * @author  Martin Entlicher
 */
public final class IntegritySupportMaintainer extends Object
                                              implements PropertyChangeListener,
                                                         VetoableChangeListener {
    private static Map VOISMap = new WeakHashMap();
    
    private FileSystem fileSystem;
    private VcsOISActivator objectIntegrityActivator;
    private VcsObjectIntegritySupport objectIntegritySupport;
    private PropertyChangeListener vOISChangeListener;

    /**
     * Create a new IntegritySupportMaintainer.
     * @param fileSystem The FileSystem
     * @param objectIntegrityActivator The activator of VcsObjectIntegritySupport.
     */
    public IntegritySupportMaintainer(FileSystem fileSystem,
                                      VcsOISActivator objectIntegrityActivator) {
        this.fileSystem = fileSystem;
        this.objectIntegrityActivator = objectIntegrityActivator;
        initVOIS();
        fileSystem.addVetoableChangeListener(WeakListener.vetoableChange(this, fileSystem));
        fileSystem.addPropertyChangeListener(WeakListener.propertyChange(this, fileSystem));
    }

    private synchronized void initVOIS() {
        objectIntegritySupport = (VcsObjectIntegritySupport) fileSystem.getRoot().getAttribute(VcsObjectIntegritySupport.ATTRIBUTE_NAME);
        if (objectIntegritySupport == null) {
            objectIntegritySupport = new VcsObjectIntegritySupport();
        }
        vOISChangeListener = WeakListener.propertyChange(this, objectIntegritySupport);
        objectIntegrityActivator.activate(objectIntegritySupport);
        objectIntegritySupport.addPropertyChangeListener(vOISChangeListener);
        synchronized (VOISMap) {
            VOISMap.put(fileSystem, objectIntegritySupport);
        }
    }
    
    /**
     * Return the active VCS object integrity support for the given file system.
     */
    public static VcsObjectIntegritySupport findObjectIntegritySupport(FileSystem fileSystem) {
        synchronized (VOISMap) {
            return (VcsObjectIntegritySupport) VOISMap.get(fileSystem);
        }
    }

    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (FileSystem.PROP_ROOT.equals(propertyName)) {
            initVOIS();
        } else if (VcsObjectIntegritySupport.PROPERTY_FILES_CHANGED.equals(propertyName)) {
            //System.out.println("IntegritySupportMaintainer.propertyChange("+propertyName+"), SAVING "+evt.getSource());
            try {
                fileSystem.getRoot().setAttribute(VcsObjectIntegritySupport.ATTRIBUTE_NAME,
                                                  (VcsObjectIntegritySupport) evt.getSource());
            } catch (java.io.IOException ioex) {
                org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ioex);
            }
        }
    }

    /** This method gets called when a constrained property is changed.
     *
     * @param     evt a <code>PropertyChangeEvent</code> object describing the
     *   	      event source and the property that has changed.
     * @exception PropertyVetoException if the recipient wishes the property
     *              change to be rolled back.
     */
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (FileSystem.PROP_ROOT.equals(evt.getPropertyName())) {
            synchronized (this) {
                if (objectIntegritySupport == null) return ;
                synchronized (VOISMap) {
                    VOISMap.remove(fileSystem);
                }
                objectIntegritySupport.removePropertyChangeListener(vOISChangeListener);
                objectIntegritySupport.deactivate();
                vOISChangeListener = null;
                /*
                try {
                    fileSystem.getRoot().setAttribute(VcsObjectIntegritySupport.ATTRIBUTE_NAME, objectIntegritySupport);
                } catch (java.io.IOException ioex) {
                    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ioex);
                }
                 */
                objectIntegritySupport = null;
            }
        }
    }
    
}
