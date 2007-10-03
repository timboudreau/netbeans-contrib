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

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.vcscore.VcsModule;

import org.openide.ErrorManager;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

import org.netbeans.modules.masterfs.providers.MountSupport;
import org.netbeans.modules.masterfs.providers.AutoMountProvider;
import org.netbeans.modules.masterfs.providers.FileSystemProvider;
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
                                                                VetoableChangeListener,
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
    
    public void shutdown() {
        //System.out.println("VcsFSProvider("+hashCode()+").shutdown");
        FSInfo[] infos = FSRegistry.getDefault().getRegistered();
        for (int i = 0; i < infos.length; i++) {
            FSInfo fsInfo = infos[i];
            fsInfo.removePropertyChangeListener(VcsFSProvider.this);
            fsInfo.removeVetoableChangeListener(VcsFSProvider.this);
            if (!fsInfo.isControl()) continue;
            FileSystem fs = fsInfo.getExistingFileSystem();
            if (fs != null) {
                try {
                    mountSupport.unmount(fs);
                    unmountedFSNotify(fs);
                } catch (IOException ioex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
                }
            }
        }
    }
    
    private void mountRegistered(FSInfo[] infos) {
        for (int i = 0; i < infos.length; i++) {
            FSInfo fsInfo = infos[i];
            if (!fsInfo.isControl()) {
                fsInfo.addPropertyChangeListener(VcsFSProvider.this);
                fsInfo.addVetoableChangeListener(VcsFSProvider.this);
                continue; // Skip the infos that are not controled
            }
            FileSystem fs = fsInfo.getFileSystem();
            if (fs == null) {
                FSRegistry.getDefault().unregister(fsInfo); // remove invalid FS Infos
                continue;
            }
            try {
                mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fs);
                fsInfo.addPropertyChangeListener(VcsFSProvider.this);
                fsInfo.addVetoableChangeListener(VcsFSProvider.this);
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
                info = pool.findFilesystemInfo(FileUtil.normalizeFile(new File(rootPath)));
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
        root = FileUtil.normalizeFile(root);
        if (root.getParent() != null) {
            if (!root.isDirectory()) return false;
        }
        final FSInfo fsInfo = pool.findFilesystemInfo(FileUtil.normalizeFile(new File(rootPath)));
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
                    fsInfo.addVetoableChangeListener(VcsFSProvider.this);
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
                info = pool.findFilesystemInfo(FileUtil.normalizeFile(new File(rootPath)));
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
    
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (FSInfo.PROP_ROOT.equals(evt.getPropertyName())) {
            Object source = evt.getSource();
            if (source instanceof FSInfo) {
                FSInfo fsInfo = (FSInfo) source;
                if (mountSupport != null) {
                    try {
                        if (fsInfo.isControl()) {
                            //System.out.println("Unmounting "+fsInfo.getFileSystem());
                            FileSystem fs = fsInfo.getFileSystem();
                            if (fs != null) {
                                mountSupport.unmount(fs);
                            }
                        }
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ioex);
                    }
                }
            }
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
                            FileSystem fs = fsInfo.getExistingFileSystem();
                            if (fs != null) {
                                mountSupport.unmount(fs); // Unmount FS that is not controlled any more
                            }
                        } else {
                            FileSystem fs = fsInfo.getFileSystem();
                            if (fs != null) {
                                mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fs);
                            }
                        }
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ioex);
                    }
                }
            }
        }
        if (FSInfo.PROP_ROOT.equals(evt.getPropertyName())) {
            Object source = evt.getSource();
            if (source instanceof FSInfo) {
                FSInfo fsInfo = (FSInfo) source;
                if (mountSupport != null) {
                    try {
                        if (fsInfo.isControl()) {
                            //System.out.println("new info path is "+fsInfo.getFSRoot()+", FS path is "+org.openide.filesystems.FileUtil.toFile(fsInfo.getFileSystem().getRoot()).getAbsolutePath());
                            FileSystem fs = fsInfo.getFileSystem();
                            if (fs != null) {
                                mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fs);
                            }
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
            if (fs != null) {
                mountSupport.mount(fsInfo.getFSRoot().getAbsolutePath(), fs);
                fsInfo.addPropertyChangeListener(VcsFSProvider.this);
                fsInfo.addVetoableChangeListener(VcsFSProvider.this);
                mountedFSNotify(fs);
            }
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
        }
    }
    
    public void fsRemoved(FSRegistryEvent ev) {
        if (PROPAGATION_IGNORE_FSREGISTRY_EVENT == ev.getPropagationId()) return ;
        FSInfo fsInfo = ev.getInfo();
        try {
            FileSystem fs = fsInfo.getFileSystem();
            if (fs != null) {
                mountSupport.unmount(fs);
                fsInfo.removePropertyChangeListener(VcsFSProvider.this);
                fsInfo.removeVetoableChangeListener(VcsFSProvider.this);
                unmountedFSNotify(fs);
            }
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
        }
    }
    
}
