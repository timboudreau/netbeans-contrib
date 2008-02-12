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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.encoder.coco.ui.wizard;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 * The wizard panel for retrieving COBOL Copybooks.
 * 
 * @author Jun Xu
 */
public class RetrieveCopybookWizardPanel1 implements WizardDescriptor.Panel,
        ChangeListener {

    private final RetrieveCopybookVisualPanel1 mVisualPanel;
    private WizardDescriptor mWizardDescriptor;
    
    public RetrieveCopybookWizardPanel1() {
        mVisualPanel = new RetrieveCopybookVisualPanel1(this);
        mVisualPanel.setSourceType(PropertyValue.FROM_URL);
        mVisualPanel.addChangeListener(this);
    }

    public Component getComponent() {
        return mVisualPanel;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    }

    public void readSettings(Object settings) {
        mWizardDescriptor = (WizardDescriptor) settings;
        FileObject fileObj = (FileObject)
                mWizardDescriptor.getProperty(PropertyKey.CURRENT_FOLDER);
        if (fileObj != null) {
            mVisualPanel.setTargetLocation(FileUtil.toFile(fileObj).getPath());
        }
    }

    public void storeSettings(Object settings) {
        WizardDescriptor descriptor = (WizardDescriptor) settings;
        descriptor.putProperty(PropertyKey.SOURCE_TYPE,
                mVisualPanel.getSourceType());
        descriptor.putProperty(PropertyKey.SOURCE_LOCATION,
                mVisualPanel.getSourceLocation());
        descriptor.putProperty(PropertyKey.TARGET_FOLDER,
                mVisualPanel.getTargetFolder());
        descriptor.putProperty(PropertyKey.OVERWRITE_EXIST,
                new Boolean(mVisualPanel.getOverwriteExist()));
    }

    public boolean isValid() {
        String targetFolder = mVisualPanel.getTargetFolder();
        if (targetFolder == null || targetFolder.length() == 0) {
            return false;
        }
        String sourceLocation = mVisualPanel.getSourceLocation();
        if (PropertyValue.FROM_FILE.equals(mVisualPanel.getSourceType())) {
            File file = new File(sourceLocation);
            if (!file.isDirectory() && !file.isFile()) {
                return false;
            }
            if (file.isFile() && !file.getName().endsWith(".cpy")) {
                return false;
            }
            return true;
        } else if (PropertyValue.FROM_URL.equals(
                mVisualPanel.getSourceType())) {
            try {
                java.net.URL url = new java.net.URL(sourceLocation);
                String path = url.getPath();
                if (path == null || path.length() < 4
                        || !path.endsWith(".cpy")) {
                    return false;
                }
            } catch (MalformedURLException ex) {
                return false;
            }
            return true;
        }
        throw new IllegalArgumentException(
                "Unknown source type: " + mVisualPanel.getSourceType());
    }

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
    
    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }
}
