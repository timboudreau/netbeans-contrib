/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.usertasks;

import java.io.IOException;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.FileSystemAction;
import org.openide.actions.OpenAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.SaveAsTemplateAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;

import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;


/**
 *  Recognizes TaskList files such as iCalendar files that the user
 *  can browse, open etc.
 *
 * @author Tor Norbye
*/

public final class TaskListLoader extends UniFileLoader {
    
    /** Serial version number */
    static final long serialVersionUID = 1L;

    /** store the popup menu actions here */
    protected static SystemAction[] standardActions;

    /** The suffix list for tasklists. Does not include .xml which we
        handle separately. */
    private static final String[] hdrExtensions = { "ics", "xcs" }; //NOI18N
    
    public TaskListLoader() {
        super("org.netbeans.modules.tasklist.usertasks.TaskListDataObject");//NOI18N

	// These extensions MUST match the ones in the editor kits...
	ExtensionList extensions = new ExtensionList();
        extensions.addExtension("ics"); // NOI18N   iCalendar
        extensions.addExtension("xcs"); // NOI18N   xCalendar
        setExtensions(extensions);
    }

    /**
     *  Defer creating the SystemAction array until its actually needed.
     */
    protected SystemAction[] createDefaultActions() {
	return new SystemAction[] {
	    SystemAction.get(OpenAction.class),
	    SystemAction.get(FileSystemAction.class),
	    null,
	    SystemAction.get(CutAction.class),
	    SystemAction.get(CopyAction.class),
	    SystemAction.get(PasteAction.class),
	    null,
	    SystemAction.get(DeleteAction.class),
	    null,
	    SystemAction.get(SaveAsTemplateAction.class),
	    null,
	    SystemAction.get(ToolsAction.class),
	    SystemAction.get(PropertiesAction.class),
	};
    }


    /**
     *  Return the SystemAction[]s array. Create it and store it if needed.
     *
     *  @return The SystemAction[] array
     */
    protected SystemAction[] defaultActions() {
	if (standardActions != null) {
	    return standardActions;
	} else {
	    synchronized(getClass()) {
		if (standardActions == null) {
		    standardActions = createDefaultActions();
		}
	    }
	}
	return standardActions;
    }


    /** set the default display name */
    protected String defaultDisplayName() {
	return NbBundle.getMessage(TaskListLoader.class,
                                   "TaskListLoader_Name"); // NOI18N
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile)
	throws DataObjectExistsException, IOException {
	return new TaskListDataObject(primaryFile, this);
    }
}

