/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
 * Recognizes TaskList files such as iCalendar files that the user
 * can browse, open etc.
 *
 * @author Tor Norbye, Trond Norbye
 * @author tl
 */
public final class TaskListLoader extends UniFileLoader {
    
    /** Serial version number */
    static final long serialVersionUID = 1L;

    /** store the popup menu actions here */
    protected static SystemAction[] standardActions;

    /**
     * Creates a new loader.
     */
    public TaskListLoader() {
        super("org.netbeans.modules.tasklist.usertasks.TaskListDataObject"); // NOI18N

	// These extensions MUST match the ones in the editor kits...
	ExtensionList extensions = new ExtensionList();
        extensions.addExtension("ics"); // NOI18N   iCalendar
        setExtensions(extensions);
    }

    /**
     * Defer creating the SystemAction array until its actually needed.
     * 
     * @return returns actions for this file type
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

