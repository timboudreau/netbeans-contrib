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


package org.netbeans.modules.tasklist.bugs.issues;

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
 * Recognizes TaskList files such as iCalendar files that the user
 * can browse, open etc.
 *
 * @author Tor Norbye, Trond Norbye
 */
public final class IssuesLoader extends UniFileLoader {
    
    /** Serial version number */
    static final long serialVersionUID = 1L;

    /** store the popup menu actions here */
    protected static SystemAction[] standardActions;

    public IssuesLoader() {
        super("org.netbeans.modules.tasklist.bugs.issues.IssuesDataObject");//NOI18N

	// These extensions MUST match the ones in the editor kits...
	ExtensionList extensions = new ExtensionList();
        extensions.addExtension("issues"); // NOI18N   iCalendar
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
	return NbBundle.getMessage(IssuesLoader.class,
                                   "BugsLoader_Name"); // NOI18N
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile)
	throws DataObjectExistsException, IOException {
	return new IssuesDataObject(primaryFile, this);
    }
}

