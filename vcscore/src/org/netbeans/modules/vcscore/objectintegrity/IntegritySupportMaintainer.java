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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
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
                                                         VetoableChangeListener,
                                                         Runnable {
    public static final String DB_FILE_NAME = ".nbintdb"; // NOI18N
    private static Map VOISMap = new WeakHashMap();
    private static final int SAVER_SCHEDULE_TIME = 500;
    
    private FileSystem fileSystem;
    private VcsOISActivator objectIntegrityActivator;
    private VcsObjectIntegritySupport objectIntegritySupport;
    private PropertyChangeListener vOISChangeListener;
    private RequestProcessor.Task saverTask;
    private Map voisToSave;
    private int saverScheduleTime = SAVER_SCHEDULE_TIME;

    /**
     * Create a new IntegritySupportMaintainer.
     * @param fileSystem The FileSystem
     * @param objectIntegrityActivator The activator of VcsObjectIntegritySupport.
     */
    public IntegritySupportMaintainer(FileSystem fileSystem,
                                      VcsOISActivator objectIntegrityActivator) {
        this.fileSystem = fileSystem;
        this.objectIntegrityActivator = objectIntegrityActivator;
        this.saverTask = RequestProcessor.createRequest(this);
        saverTask.setPriority(Thread.MIN_PRIORITY);
        this.voisToSave = new HashMap();
        initVOIS();
        fileSystem.addVetoableChangeListener(WeakListener.vetoableChange(this, fileSystem));
        fileSystem.addPropertyChangeListener(WeakListener.propertyChange(this, fileSystem));
    }

    private synchronized void initVOIS() {
        objectIntegritySupport = new VcsObjectIntegritySupport(new IntegritySupportMaintainer.VOISInitializer(fileSystem.getRoot()));
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
            synchronized (voisToSave) {
                voisToSave.put(fileSystem.getRoot(), evt.getSource());
                saverTask.schedule(saverScheduleTime);
                //System.out.println("                                        Saver Scheduled At: "+new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(new java.util.Date(System.currentTimeMillis())));
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
    
    /**
     * Save the VcsObjectIntegritySupport as an attribute of a FileObject.
     *
     * @see     java.lang.Thread#run()
     */
    public void run() {
        Map toSave = new HashMap();
        synchronized (voisToSave) {
            toSave.putAll(voisToSave);
            voisToSave.clear();
        }
        //System.out.println("ISM saving START: "+new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(new java.util.Date(System.currentTimeMillis())));
        long start = System.currentTimeMillis();
        for (Iterator it = toSave.keySet().iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            VcsObjectIntegritySupport vois = (VcsObjectIntegritySupport) toSave.get(fo);
            //fo.setAttribute(VcsObjectIntegritySupport.ATTRIBUTE_NAME, vois);
            File folder = FileUtil.toFile(fo);
            if (folder != null) {
                File dbFile = new File(folder, DB_FILE_NAME);
                File dbSaveFile = new File(folder, DB_FILE_NAME+"~");
                ObjectOutputStream oout = null;
                boolean ok = false;
                try {
                    oout = new ObjectOutputStream(new FileOutputStream(dbSaveFile));
                    oout.writeObject(vois);
                } catch (java.io.IOException ioex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
                } finally {
                    if (oout != null) {
                        try {
                            oout.close();
                            ok = true;
                        } catch (IOException ioex) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
                        }
                    }
                }
                if (ok) {
                    dbFile.delete();
                    dbSaveFile.renameTo(dbFile);
                }
            }
        }
        long end = System.currentTimeMillis();
        saverScheduleTime = Math.max(SAVER_SCHEDULE_TIME, (int) (end - start));
        //System.out.println("           END  : "+new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(new java.util.Date(System.currentTimeMillis())));
    }
    
    private static final class VOISInitializer extends Object implements java.security.PrivilegedAction {
        
        private FileObject rootFO;
        
        public VOISInitializer(FileObject rootFO) {
            this.rootFO = rootFO;
        }
        
        public Object run() {
            File folder = FileUtil.toFile(rootFO);
            if (folder != null) {
                File dbFile = new File(folder, DB_FILE_NAME);
                if (!dbFile.exists()) {
                    dbFile = new File(folder, DB_FILE_NAME+"~");
                }
                if (dbFile.exists() && dbFile.canRead()) {
                    ObjectInputStream oin = null;
                    Object vois = null;
                    try {
                        oin = new ObjectInputStream(new FileInputStream(dbFile));
                        vois = oin.readObject();
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
                        // The vois will remain null
                    } catch (ClassNotFoundException cnfex) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, cnfex);
                        // The vois will remain null
                    } finally {
                        if (oin != null) {
                            try {
                                oin.close();
                            } catch (IOException ioex) {}
                        }
                    }
                    return vois;
                }
            }
            return null;
            //return rootFO.getAttribute(VcsObjectIntegritySupport.ATTRIBUTE_NAME);
        }
        
    }
    
}
