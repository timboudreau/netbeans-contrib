/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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
package org.netbeans.modules.clearcase.ui.update;

import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.client.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * Updates selected files/folders in the snapshot view.
 * 
 * @author Maros Sandor
 */
public class UpdateAction extends AbstractAction implements NotificationListener {
    
    private final VCSContext context;

    public UpdateAction(String name, VCSContext context) {
        this.context = context;
        putValue(Action.NAME, name);
    }
    
    @Override
    public boolean isEnabled() {
        Set<File> roots = context.getRootFiles();
        for (File file : roots) {
            // TODSO consider this as a HACK - cache the info if file in shapshot or not 
            if( Clearcase.getInstance().getTopmostSnapshotViewAncestor(file) == null ) {
                return false;
            }                
        }
        return true;
    }    
    
    public void actionPerformed(ActionEvent e) {
        Set<File> files = context.computeFiles(updateFileFilter);
        Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Updating...",
                new UpdateCommand(files.toArray(new File[files.size()]), this, new OutputWindowNotificationListener())));
    }

    private static final FileFilter updateFileFilter = new FileFilter() {
        public boolean accept(File pathname) {
            return true;
        }
    };

    public void commandStarted() {
    }

    public void outputText(String line) {
    }

    public void errorText(String line) {
    }

    public void commandFinished() {
    }
}
