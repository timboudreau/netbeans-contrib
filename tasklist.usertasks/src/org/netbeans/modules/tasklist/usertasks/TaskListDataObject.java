/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
 
package org.netbeans.modules.tasklist.usertasks;

import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;


/**
 * Represents a tasklist object in the Repository.
 *
 * @author Tor Norbye, Trond Norbye
 */
public class TaskListDataObject extends MultiDataObject implements OpenCookie {

    /** Serial version number */
    static final long serialVersionUID = 1L;

    public TaskListDataObject(FileObject pf, TaskListLoader loader)
			    throws DataObjectExistsException {
	super(pf, loader);
    	CookieSet cookies = getCookieSet();
	cookies.add(this); // OpenCookie
    }
  
    protected Node createNodeDelegate() {
	return new TaskListDataNode(this);
    }

    // Implements OpenCookie
    
    /** Invokes the open action. */
    public void open() {
	UserTaskView view = UserTaskView.findListView(getPrimaryEntry().getFile());
        if (view == null) {
            UserTaskList tl = new UserTaskList();
            if (tl.readFile(getPrimaryEntry().getFile())) {
                view = new UserTaskView(tl, false);
                view.showInMode();
            }
        } else {
            // This view already exist, show it...
            view.showInMode();
        }
    }   
}
