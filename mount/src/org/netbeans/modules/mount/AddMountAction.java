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

package org.netbeans.modules.mount;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.openide.windows.WindowManager;

/**
 * Adds a new mount.
 * @author Jesse Glick
 */
final class AddMountAction extends AbstractAction {
    
    private final boolean archives;
    
    public AddMountAction(boolean archives) {
        super(archives ? "Add ZIP/JAR(s)..." : "Add Folder..."/* XXX icon */);
        this.archives = archives;
    }

    public void actionPerformed(ActionEvent e) {
        // XXX remember last start dir
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(archives ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(archives);
        // XXX set appropriate filter if archives
        if (chooser.showDialog(WindowManager.getDefault().getMainWindow(), "Add") != JFileChooser.APPROVE_OPTION) {
            return;
        }
        if (archives) {
            File[] files = chooser.getSelectedFiles();
            for (int i = 0; i < files.length; i++) {
                MountList.DEFAULT.addArchive(files[i]);
            }
        } else {
            MountList.DEFAULT.addFolder(chooser.getSelectedFile());
        }
    }
    
}
