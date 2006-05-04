/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.buildmonitor;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.*;
import org.openide.loaders.*;

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
    
    /** Creates a new instance of BuildMonitorPanel */
    private BuildMonitorPanel() {
	super();
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	FileObject dir = monitorDirectory();
        dir.addFileChangeListener(this);
	buildPanel();
    }
    
    private void buildPanel() {
	DataFolder monitorFolder = DataFolder.findFolder(monitorDirectory());
 	DataObject[] children = monitorFolder.getChildren();
        for (int i = 0; i < children.length; i++) {
            DataObject dataObject = children[i];
            InstanceCookie ic = (InstanceCookie)dataObject.getCookie(InstanceCookie.class);
	    if (ic == null)
		continue;
	    try {
		BuildMonitor monitor = (BuildMonitor)ic.instanceCreate();
                if (monitor.getName() == null)
                    monitor.setName((String)dataObject.getPrimaryFile().getAttribute("name"));
		BuildStatus status = new BuildStatus(monitor);
		add(status);
		if (i+1 < children.length)
		    add(Box.createHorizontalStrut(10));
	    } catch (Exception e) {
		ErrorManager.getDefault().notify(e);
	    }
        }
	
    }
    
    private void rebuildPanel() {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		removeAll();
		buildPanel();
	    }
	});
    }
    private static FileObject monitorDirectory() {
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem();
        return dfs.findResource("/Services/BuildMonitor"); //NOI18N
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
    public void fileDataCreated(FileEvent fe) {}
}
