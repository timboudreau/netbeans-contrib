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

package org.netbeans.modules.pathtools;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
final class PathtoolsOptionsPanelController extends OptionsPanelController {
    
    private PathtoolsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;
    
    static final String PROP_FOLDER_EXPLORE_COMMAND = "folderExploreCommand"; // NOI18N
    static final String PROP_FILE_EXPLORE_COMMAND = "fileExploreCommand"; // NOI18N
    static final String PROP_FOLDER_SHELL_COMMAND = "folderShellCommand"; // NOI18N
    static final String PROP_FILE_SHELL_COMMAND = "fileShellCommand"; // NOI18N
    static final String PROP_FOLDER_EDIT_COMMAND = "folderEditCommand"; // NOI18N
    static final String PROP_FILE_EDIT_COMMAND = "fileEditCommand"; // NOI18N
        
    private static final String nautilusFileExplorerPath="/usr/bin/nautilus";
    private static final String konquerorFileExplorerPath="/usr/bin/konqueror";
    
    private static String DEFAULT_FOLDER_EXPLORE_COMMAND = ""; // NOI18N
    private static String DEFAULT_FILE_EXPLORE_COMMAND = ""; // NOI18N
    private static String DEFAULT_FOLDER_SHELL_COMMAND = ""; // NOI18N
    private static String DEFAULT_FILE_SHELL_COMMAND = ""; // NOI18N
    private static String DEFAULT_FOLDER_EDIT_COMMAND = ""; // NOI18N
    private static String DEFAULT_FILE_EDIT_COMMAND = ""; // NOI18N
    
    static {
        if (Utilities.isWindows()) {
            DEFAULT_FOLDER_EXPLORE_COMMAND = "explorer /e, \"{path}\""; // NOI18N
            DEFAULT_FILE_EXPLORE_COMMAND = "explorer /e,/select, \"{path}\""; // NOI18N
            DEFAULT_FOLDER_SHELL_COMMAND = "cmd /C start cmd /K cd /D \"{path}\""; // NOI18N
            DEFAULT_FILE_SHELL_COMMAND = "cmd /C start cmd /K cd /D \"{parent-path}\""; // NOI18N
            DEFAULT_FOLDER_EDIT_COMMAND = "explorer /e, \"{path}\""; // NOI18N
            DEFAULT_FILE_EDIT_COMMAND = "cmd /C start notepad \"{path}\""; // NOI18N
        } else if ((Utilities.getOperatingSystem() & Utilities.OS_LINUX) !=0) {
            File nautilus =new File(nautilusFileExplorerPath);
            File konqueror=new File(konquerorFileExplorerPath);
            String commandPrefix = null;
            if (nautilus.exists()){
                DEFAULT_FOLDER_EXPLORE_COMMAND = nautilusFileExplorerPath + " \"file:///{path}\""; // NOI18N
                DEFAULT_FILE_EXPLORE_COMMAND = nautilusFileExplorerPath + " \"file:///{parent-path}\""; // NOI18N
            } else if (konqueror.exists()){
                DEFAULT_FOLDER_EXPLORE_COMMAND = konquerorFileExplorerPath + " \"file:///{path}\""; // NOI18N
                DEFAULT_FILE_EXPLORE_COMMAND = konquerorFileExplorerPath + " \"file:///{parent-path}\""; // NOI18N
            } else {
                DEFAULT_FOLDER_EXPLORE_COMMAND = ""; // NOI18N
                DEFAULT_FILE_EXPLORE_COMMAND = ""; // NOI18N
            }
        } else if ((Utilities.getOperatingSystem() & Utilities.OS_SOLARIS) !=0) {
            // May be JDS
            File nautilus=new File(nautilusFileExplorerPath);
            if (nautilus.exists()){
                DEFAULT_FOLDER_EXPLORE_COMMAND = nautilusFileExplorerPath + " \"file:///{path}\""; // NOI18N
                DEFAULT_FILE_EXPLORE_COMMAND = nautilusFileExplorerPath + " \"file:///{parent-path}\""; // NOI18N
            } else {
                DEFAULT_FOLDER_EXPLORE_COMMAND = ""; // NOI18N
                DEFAULT_FILE_EXPLORE_COMMAND = ""; // NOI18N
            }
        } else if ((Utilities.getOperatingSystem() & Utilities.OS_MAC) !=0) {
            DEFAULT_FOLDER_EXPLORE_COMMAND = "/usr/bin/open \"{path}\""; // NOI18N
            DEFAULT_FILE_EXPLORE_COMMAND =   "/usr/bin/open \"{parent-path}\""; // NOI18N
        } else if (Utilities.isUnix()) {
            // TODO
        }
    }

    public PathtoolsOptionsPanelController() {
    }
    
    static String getDEFAULT_FOLDER_EXPLORE_COMMAND() {
        return DEFAULT_FOLDER_EXPLORE_COMMAND;
    }
    
    static String getDEFAULT_FILE_EXPLORE_COMMAND() {
        return DEFAULT_FILE_EXPLORE_COMMAND;
    }

    static String getDEFAULT_FOLDER_SHELL_COMMAND() {
        return DEFAULT_FOLDER_SHELL_COMMAND;
    }
    
    static String getDEFAULT_FILE_SHELL_COMMAND() {
        return DEFAULT_FILE_SHELL_COMMAND;
    }
    
    static String getDEFAULT_FOLDER_EDIT_COMMAND() {
        return DEFAULT_FOLDER_EDIT_COMMAND;
    }
    
    static String getDEFAULT_FILE_EDIT_COMMAND() {
        return DEFAULT_FILE_EDIT_COMMAND;
    }
    
    public void update() {
        getPanel().load();
        changed = false;
    }
    
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }
    
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }
    
    public boolean isValid() {
        return getPanel().valid();
    }
    
    public boolean isChanged() {
        return changed;
    }
    
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }
    
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    private PathtoolsPanel getPanel() {
        if (panel == null) {
            panel = new PathtoolsPanel(this);
        }
        return panel;
    }
    
    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
    
}
