/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;

import org.openide.ErrorManager;
import org.openide.filesystems.FileSystem;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

import org.netbeans.api.masterfs.MountSupport;
import org.netbeans.spi.masterfs.AutoMountProvider;
import org.netbeans.spi.masterfs.FileSystemProvider;
import org.netbeans.modules.masterfs.*;

import org.netbeans.modules.vcscore.registry.FSRegistryEvent;
import org.netbeans.modules.vcscore.registry.FSRegistryListener;
import org.netbeans.modules.vcscore.runtime.RuntimeCommandsProvider;

/**
 * Provider of VCS filesystems for recognized folders.
 *
 * @author  Martin Entlicher
 */
public class VcsFSProvider extends AutoMountProvider implements FileSystemProvider,
                                                                PropertyChangeListener,
                                                                FSRegistryListener {
    /** If the FSRegistryEvent should be ignored, the propagationId property
     * is set to this constant. */
    private static final Object PROPAGATION_IGNORE_FSREGISTRY_EVENT = new Object();
    
    private FSRecognizerPool pool;
    //private AutoMountProviderImpl autoProvider;
    private MountSupport mountSupport;
    private String lastFSRootRecognized = null;
    private Reference lastFSInfoCached = new WeakReference(null);
    private final Object lastFSRecognizedLock = new Object();
    
    /** Creates a new instance of CommandLineFileSystemProvider */
    public VcsFSProvider() {
        this.pool = FSRecognizerPool.getDefault();
        FSRegistry.getDefault().addFSRegistryListener((FSRegistryListener) WeakListeners.create(FSRegistryListener.class, this, FSRegistry.getDefault()));
        //this.autoProvider = new AutoMountProviderImpl();
        //this.mountSupport = new MountSupportImpl();
    }
    
    /**
     * HostFileSystem invokes this method to pass impl. of MountSupport, that can
     * be kept and used later. 
     * @param mSupport impl. of MountSupport 
     * @return  true if there is supported AutoMountProvider 
     */    
    public AutoMountProvider initialize (MountSupport mSupport) {
        this.mountSupport = mSupport;
        mountRegistered(FSRegistry.getDefault().getRegistered());
        //return this;  TODO Return this to activate the auto-recognition
        return null;
    }
    
    private void mountRegistered(FSInfo[] infos) {
        for (int i = 0; i < infos.length; i++) {
            FSInfo fsInfo = infos[i];
            FileSystem fs = fsInfo.getFileSystem();
            try {
                mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fs);
                fsInfo.addPropertyChangeListener(VcsFSProvider.this);
                mountedFSNotify(fs);
            } catch (IOException ioex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
            }
        }
    }
    
    /**
     * Creates filesystem with root defined by rootPath
     * @param rootPath, that corresponds to java.io.File.getAbstractPath
     * @return instance of FileSystem  only if also method isRootOfFileSystem returns true,
     * else returns null
     */
    public FileSystem createFileSystem(String rootPath) throws IOException, PropertyVetoException {
        //System.out.println("VcsFSProvider.createFileSystem("+rootPath+")");
        FSInfo info = null;
        synchronized (lastFSRecognizedLock) {
            if (rootPath.equals(lastFSRootRecognized)) {
                info = (FSInfo) lastFSInfoCached.get();
            } else {
                info = pool.findFilesystemInfo(new File(rootPath));
                if (info != null) {
                    FSRegistry.getDefault().register(info, PROPAGATION_IGNORE_FSREGISTRY_EVENT, true);
                }
            }
        }
        if (info != null) {
            FileSystem fs = info.getFileSystem();
            if (fs != null) mountedFSNotify(fs);
            return fs;
        } else {
            return null;
        }
    }
    
    /**
     * @param rootPath, that corresponds to java.io.File.getAbstractPath
     * @return true if rootPath is expected to be root of supported FileSystem
     */
    public boolean isRootOfFileSystem(final String rootPath) {
        synchronized (lastFSRecognizedLock) {
            if (rootPath.equals(lastFSRootRecognized)) {
                FSInfo info = (FSInfo) lastFSInfoCached.get();
                if (info != null) return true;
            }
        }
        File root = new File(rootPath);
        if (root.getParent() != null) {
            if (!root.isDirectory()) return false;
        }
        final FSInfo fsInfo = pool.findFilesystemInfo(new File(rootPath));
        //System.out.println("isRootOfFileSystem("+rootPath+") = "+fsInfo);
        if (fsInfo != null && fsInfo.isControl()) {
            if (!root.equals(fsInfo.getFSRoot())) {
                //System.out.println("  HAVE Different Root: "+fsInfo.getFSRoot());
                if (mountSupport != null) {
                    //System.out.println("  Mounting manually.");
                    //RequestProcessor.getDefault().post(new Runnable() {
                    //    public void run() {
                            synchronized (lastFSRecognizedLock) {
                                lastFSRootRecognized = fsInfo.getFSRoot().getAbsolutePath();
                                lastFSInfoCached = new SoftReference(fsInfo);
                            }
                            FSRegistry.getDefault().register(fsInfo); // It will mount it as well - fsAdded() will be called
                    //    }
                    //});
                }
                return false;
            } else {
                // Assure, that we register the FS info only once!
                if (!FSRegistry.getDefault().isRegistered(fsInfo)) {
                    fsInfo.addPropertyChangeListener(VcsFSProvider.this);
                    FSRegistry.getDefault().register(fsInfo, PROPAGATION_IGNORE_FSREGISTRY_EVENT, true);
                }
                return true;
            }
        } else {
            return false;
        }
    }
    
    public Image getIcon(String rootPath, int iconType) {
        FSInfo info;
        synchronized (lastFSRecognizedLock) {
            if (rootPath.equals(lastFSRootRecognized)) {
                info = (FSInfo) lastFSInfoCached.get();
            } else {
                info = pool.findFilesystemInfo(new File(rootPath));
                if (info != null) {
                    FSRegistry.getDefault().register(info, PROPAGATION_IGNORE_FSREGISTRY_EVENT, true);
                }
            }
        }
        if (info != null) {
            return info.getIcon();
        } else {
            return null;
        }
    }

    /**
     * A notification method, that is called when a FileSystem was "mounted" to
     * the HostFileSystem.
     */
    private static void mountedFSNotify(FileSystem fs) {
        RuntimeCommandsProvider runtimeProvider = RuntimeCommandsProvider.findProvider(fs);
        //System.out.println("mountedFSNotify("+fs+"), runtimeProvider = "+runtimeProvider);
        if (runtimeProvider != null) {
            runtimeProvider.register();
        }
    }

    /**
     * A notification method, that is called when a FileSystem was "unmounted" from
     * the HostFileSystem.
     */
    private static void unmountedFSNotify(FileSystem fs) {
        RuntimeCommandsProvider runtimeProvider = RuntimeCommandsProvider.findProvider(fs);
        //System.out.println("mountedFSNotify("+fs+"), runtimeProvider = "+runtimeProvider);
        if (runtimeProvider != null) {
            runtimeProvider.unregister();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("VcsFSProvider.propertyChange("+evt+"), prop name = "+evt.getPropertyName());
        if (FSInfo.PROP_CONTROL.equals(evt.getPropertyName())) {
            Object source = evt.getSource();
            if (source instanceof FSInfo) {
                FSInfo fsInfo = (FSInfo) source;
                if (mountSupport != null) {
                    try {
                        if (!fsInfo.isControl()) {
                            mountSupport.unmount(fsInfo.getFileSystem());
                        } else {
                            mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fsInfo.getFileSystem());
                        }
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ioex);
                    }
                }
            }
        }
    }
    
    public void fsAdded(FSRegistryEvent ev) {
        if (PROPAGATION_IGNORE_FSREGISTRY_EVENT == ev.getPropagationId()) return ;
        FSInfo fsInfo = ev.getInfo();
        try {
            FileSystem fs = fsInfo.getFileSystem();
            mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fs);
            fsInfo.addPropertyChangeListener(VcsFSProvider.this);
            mountedFSNotify(fs);
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
        }
    }
    
    public void fsRemoved(FSRegistryEvent ev) {
        if (PROPAGATION_IGNORE_FSREGISTRY_EVENT == ev.getPropagationId()) return ;
        FSInfo fsInfo = ev.getInfo();
        try {
            FileSystem fs = fsInfo.getFileSystem();
            mountSupport.unmount(fs);
            fsInfo.removePropertyChangeListener(VcsFSProvider.this);
            unmountedFSNotify(fs);
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
        }
    }
    
}
