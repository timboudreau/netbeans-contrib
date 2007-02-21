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

package org.netbeans.modules.erd.io;

import java.io.IOException;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.EditAction;
import org.openide.actions.FileSystemAction;
import org.openide.actions.OpenAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.SaveAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

public class ERDDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-erd";
    
    private static final long serialVersionUID = 1L;
    
    public ERDDataLoader() {
        super("org.netbeans.modules.erd.io.ERDDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(ERDDataLoader.class, "LBL_ERD_loader_name");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ERDDataObject(primaryFile, this);
    }
    
   /* protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }*/
    
    protected SystemAction[] defaultActions() {
		// TODO Auto-generated method stub
	return new SystemAction[]{
                SystemAction.get (OpenAction.class),
                SystemAction.get (EditAction.class),
                SystemAction.get (SaveAction.class),
                SystemAction.get (FileSystemAction.class),
                null,
                SystemAction.get (ToolsAction.class),
                SystemAction.get (PropertiesAction.class),
                SystemAction.get (CopyAction.class),
                SystemAction.get (CutAction.class),
                SystemAction.get (PasteAction.class)        
        };
        
       /* return new SystemAction[] {
				SystemAction.get(OpenAction.class),
				SystemAction.get(EditAction.class)
               
				
		};*/
	}
    
    
}
