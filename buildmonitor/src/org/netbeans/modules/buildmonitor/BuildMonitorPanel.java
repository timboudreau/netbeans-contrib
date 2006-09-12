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

package org.netbeans.modules.buildmonitor;

import java.awt.EventQueue;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * Displays one or more BuildStatus objects in the status bar.
 *
 * @author Tom Ball
 */
public class BuildMonitorPanel extends JPanel implements FileChangeListener {
    private static final BuildMonitorPanel instance = new BuildMonitorPanel();
    
    public static BuildMonitorPanel getInstance() {
	return instance;
    }

    private final FileObject dir;
    
    private BuildMonitorPanel() {
	super();
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem();
        dir = dfs.findResource("Services/BuildMonitor"); //NOI18N
        if (dir != null) {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            dir.addFileChangeListener(FileUtil.weakFileChangeListener(this, dir));
            buildPanel();
        } // else something broken...
    }
    
    private void buildPanel() {
	DataFolder monitorFolder = DataFolder.findFolder(dir);
 	DataObject[] children = monitorFolder.getChildren();
        for (int i = 0; i < children.length; i++) {
            DataObject dataObject = children[i];
            InstanceCookie ic = (InstanceCookie)dataObject.getCookie(InstanceCookie.class);
	    if (ic == null)
		continue;
	    try {
		BuildMonitor monitor = (BuildMonitor)ic.instanceCreate();
		BuildStatus status = new BuildStatus(monitor);
		add(status);
		if (i+1 < children.length)
		    add(Box.createHorizontalStrut(10));
	    } catch (Exception e) {
		ErrorManager.getDefault().notify(e);
	    }
        }
	
        revalidate();
        repaint();
    }
    
    private void rebuildPanel() {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		removeAll();
		buildPanel();
	    }
	});
    }

    public void fileDeleted(FileEvent fe) {
	rebuildPanel();
    }
    
    public void fileAttributeChanged(FileAttributeEvent fe) {
	rebuildPanel();
    }

    public void fileRenamed(FileRenameEvent fe) {
        rebuildPanel();
    }
    
    public void fileChanged(FileEvent fe) {
        rebuildPanel();
    }

    public void fileFolderCreated(FileEvent fe) {}
    public void fileDataCreated(FileEvent fe) {
        rebuildPanel();
    }
}
