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
package org.netbeans.modules.clearcase;

import java.io.File;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.netbeans.modules.versioning.spi.VCSAnnotator;
import org.netbeans.modules.versioning.spi.VCSInterceptor;
import org.netbeans.modules.versioning.spi.VersioningSystem;
import org.netbeans.modules.versioning.util.VersioningEvent;
import org.netbeans.modules.versioning.util.VersioningListener;

/**
 * Extends framework <code>VersioningSystem</code> to Clearcase module functionality.
 * 
 * @author Maros Sandor
 */
public class ClearcaseVCS extends VersioningSystem implements PropertyChangeListener, VersioningListener {

    public ClearcaseVCS() {
        putProperty(PROP_DISPLAY_NAME, "Clearcase");
        putProperty(PROP_MENU_LABEL, "Clea&rcase");
        Clearcase.getInstance().getFileStatusCache().addVersioningListener(this);
    }

    /**
     * Tests whether the file is managed by this versioning system. If it is, 
     * the method should return the topmost 
     * ancestor of the file that is still versioned.
     *  
     * @param file a file
     * @return File the file itself or one of its ancestors or null if the 
     *  supplied file is NOT managed by this versioning system
     */
    @Override
    public File getTopmostManagedAncestor(File file) {
        return Clearcase.getInstance().getTopmostManagedParent(file);
    }
    
    /**
     * Coloring label, modifying icons, providing action on file
     */
    @Override
    public VCSAnnotator getVCSAnnotator() {
        return Clearcase.getInstance().getAnnotator();
    }
    
    /**
     * Handle file system events such as delete, create, remove etc.
     */
    @Override
    public VCSInterceptor getVCSInterceptor() {
        return Clearcase.getInstance().getInterceptor();
    }

    public void propertyChange(PropertyChangeEvent event) {
        // TODO - doesn't look like we need this right now
//        if (event.getPropertyName().equals(Clearcase.PROP_ANNOTATIONS_CHANGED)) {
//            fireAnnotationsChanged((Set<File>) event.getNewValue());
//        } else if (event.getPropertyName().equals(Clearcase.PROP_VERSIONED_FILES_CHANGED)) {
//            fireVersionedFilesChanged();
//        } 
    }

    public void versioningEvent(VersioningEvent event) {
        if (event.getId() == FileStatusCache.EVENT_FILE_STATUS_CHANGED) {
            File file = (File) event.getParams()[0];
            fireStatusChanged(file);
        }
    }
    
    @Override
    public void getOriginalFile(File workingCopy, File originalFile) {
        Clearcase.getInstance().getOriginalFile(workingCopy, originalFile);
    }
}