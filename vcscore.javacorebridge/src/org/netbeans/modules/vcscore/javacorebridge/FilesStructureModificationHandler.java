/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.javacorebridge;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.modules.javacore.JMManager;

import org.netbeans.modules.vcscore.FilesModificationSupport;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSRegistryEvent;
import org.netbeans.modules.vcscore.registry.FSRegistryListener;

import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.openide.filesystems.FileSystem;

import org.openide.modules.ModuleInstall;

import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Handler of changes to files and directories structure caused by VCS operations.
 *
 * @author Martin Entlicher
 */
public class FilesStructureModificationHandler extends ModuleInstall implements FSRegistryListener,
                                                                                ChangeListener,
                                                                                LookupListener,
                                                                                PropertyChangeListener,
                                                                                Runnable {
    
    private static final long serialVersionUID = 0L;
    
    private Lookup.Result globalProvidersRes;
    private volatile Collection attachedGlobalCommandsProviders;
    
    public void restored() {
        FSRegistry.getDefault().addFSRegistryListener(this);
        FSInfo[] infos = FSRegistry.getDefault().getRegistered();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].isControl()) {
                attachTo(infos[i]);
            }
            infos[i].addPropertyChangeListener(this);
        }
        globalProvidersRes = Lookup.getDefault().lookup(new Lookup.Template(VcsCommandsProvider.class));
        attachedGlobalCommandsProviders = new HashSet();
        globalProvidersRes.addLookupListener(this);
        VcsCommandsProvider[] globalProviders = (VcsCommandsProvider[]) globalProvidersRes.allInstances().toArray(new VcsCommandsProvider[0]);
        for (int i = 0; i < globalProviders.length; i++) {
            if (globalProviders[i] instanceof FilesModificationSupport) {
                // resultChanged() might be called in between. We need to check whether it's not already added.
                if (!attachedGlobalCommandsProviders.contains(globalProviders[i])) {
                    ((FilesModificationSupport) globalProviders[i]).addFilesStructureModificationListener(this);
                    attachedGlobalCommandsProviders.add(globalProviders[i]);
                }
            }
        }
    }
    
    public void uninstalled() {
        FSInfo[] infos = FSRegistry.getDefault().getRegistered();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].isControl()) {
                detachFrom(infos[i]);
            }
            infos[i].removePropertyChangeListener(this);
        }
        if (globalProvidersRes != null) {
            globalProvidersRes.removeLookupListener(this);
            globalProvidersRes = null;
        }
        if (attachedGlobalCommandsProviders != null) {
            for (Iterator it = attachedGlobalCommandsProviders.iterator(); it.hasNext(); ) {
                FilesModificationSupport fms = (FilesModificationSupport) it.next();
                fms.removeFilesStructureModificationListener(this);
            }
            attachedGlobalCommandsProviders = null;
        }
    }

    public void fsAdded(FSRegistryEvent ev) {
        FSInfo info = ev.getInfo();
        if (info.isControl()) {
            attachTo(info);
        }
        info.addPropertyChangeListener(this);
    }

    public void fsRemoved(FSRegistryEvent ev) {
        FSInfo info = ev.getInfo();
        if (info.isControl()) {
            detachFrom(info);
        }
        info.removePropertyChangeListener(this);
    }
    
    /** The globally registered VcsCommandsProviders changed. */
    public void resultChanged(LookupEvent ev) {
        Collection newInstances = globalProvidersRes.allInstances();
        Collection old = new HashSet(attachedGlobalCommandsProviders);
        old.removeAll(newInstances);
        Collection newOnes = new HashSet(newInstances);
        newInstances.removeAll(attachedGlobalCommandsProviders);
        attachedGlobalCommandsProviders = new HashSet(newInstances);
        for (Iterator it = old.iterator(); it.hasNext(); ) {
            FilesModificationSupport fms = (FilesModificationSupport) it.next();
            fms.removeFilesStructureModificationListener(this);
        }
        for (Iterator it = newOnes.iterator(); it.hasNext(); ) {
            FilesModificationSupport fms = (FilesModificationSupport) it.next();
            fms.addFilesStructureModificationListener(this);
        }
    }
    
    private void attachTo(FSInfo info) {
        FileSystem fs = info.getFileSystem();
        if (fs instanceof FilesModificationSupport) {
            ((FilesModificationSupport) fs).addFilesStructureModificationListener(this);
        }
    }
    
    private void detachFrom(FSInfo info) {
        FileSystem fs = info.getFileSystem();
        if (fs instanceof FilesModificationSupport) {
            ((FilesModificationSupport) fs).removeFilesStructureModificationListener(this);
        }
    }

    /** Called as FilesStructureModificationListener (ChangeListener). */
    public void stateChanged(ChangeEvent e) {
        javax.swing.SwingUtilities.invokeLater(this);
    }

    /** Info control state changed. */
    public void propertyChange(PropertyChangeEvent evt) {
        if (FSInfo.PROP_CONTROL.equals(evt.getPropertyName())) {
            FSInfo info = (FSInfo) evt.getSource();
            if (info.isControl()) {
                attachTo(info);
            } else {
                detachFrom(info);
            }
        }
    }

    /** Do the actual rescan */
    public void run() {
        ((JMManager) JMManager.getManager()).startRescan();
    }

}
