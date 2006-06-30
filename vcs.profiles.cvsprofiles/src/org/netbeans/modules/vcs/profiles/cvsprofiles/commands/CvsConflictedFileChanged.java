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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.netbeans.api.vcs.FileStatusInfo;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Turbo;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * Called when a file that has "File had conflicts on merge" is changed.
 *
 * @author Martin Entlicher
 */
public class CvsConflictedFileChanged implements ActionListener {
    
    /** Creates a new instance of CvsConflictedFileChanged */
    public CvsConflictedFileChanged() {
    }

    public void actionPerformed(ActionEvent e) {
        String fileName = e.getActionCommand();
        VcsFileSystem fs = (VcsFileSystem) e.getSource();
        File file = fs.getFile(fileName);
        if (!hasConflicts(file)) {
            try {
                CvsResolveConflicts.repairEntries(file);
                FileObject fo = fs.findResource(fileName);
                if (fo != null) {
                    FileProperties fprops = Turbo.getMeta(fo);
                    String status = FileStatusInfo.MODIFIED.getName();
                    Map tranls = fs.getGenericStatusTranslation();
                    if (tranls != null) {
                        status = (String) tranls.get(status);
                        if (status == null) {
                            // There's no mapping, use the generic status name!
                            status = FileStatusInfo.MODIFIED.getName();
                        }
                    }
                    FileProperties updated = new FileProperties(fprops);
                    updated.setName(fo.getNameExt());
                    updated.setStatus(status);
                    Turbo.setMeta(fo, updated);
                }
            } catch (IOException ioex) {
                // The Entries will not be repaired at worse
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioex);
            }
        }
    }
    
    private static boolean hasConflicts(File file) {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(file));
            String line;
            int conflictMarks = 0;
            while ((line = r.readLine()) != null && conflictMarks < 7) {
                if (line.startsWith(CvsResolveConflicts.CHANGE_LEFT)) {
                    conflictMarks = conflictMarks | 1;
                }
                if (line.startsWith(CvsResolveConflicts.CHANGE_DELIMETER)) {
                    conflictMarks = conflictMarks | 2;
                }
                if (line.startsWith(CvsResolveConflicts.CHANGE_RIGHT)) {
                    conflictMarks = conflictMarks | 4;
                }
            }
            return conflictMarks == 7; // There are conflicts when there are all conflict marks.
        } catch (IOException ioex) {
            // Ignore
            return true; // Be conservative
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ioex) {}
            }
        }
    }
    
}
