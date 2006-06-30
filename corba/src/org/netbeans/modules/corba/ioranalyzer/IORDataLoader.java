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

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.actions.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;

public class IORDataLoader extends UniFileLoader {

    static final long serialVersionUID = -8245818160929345918L;

    public IORDataLoader () {
        super ("org.netbeans.modules.corba.ioranalyzer.IORDataObject");
    }

    public FileObject findPrimaryFile (FileObject fo) {
        if ("ior".equals(fo.getExt()))
            return fo;
        else
	    return null;
    }
    
    public MultiDataObject createMultiObject (FileObject fo) {
        try {
	    return new IORDataObject (fo, this);
        }catch ( DataObjectExistsException e) {
            try {
                return (MultiDataObject) e.getDataObject();
            }catch (ClassCastException cce) {
                return null;
            }
        }
    }
    
    protected SystemAction[] defaultActions () {
        return new SystemAction[] {
	    SystemAction.get (OpenAction.class),
	    SystemAction.get (FileSystemAction.class),
	    null,
	    SystemAction.get (CutAction.class),
	    SystemAction.get (CopyAction.class),
	    SystemAction.get (PasteAction.class),
	    null,
	    SystemAction.get (DeleteAction.class),
	    SystemAction.get (RenameAction.class),
            null,
            SystemAction.get (MergeAction.class),
            SystemAction.get (RefreshAction.class),
	    null,
	    SystemAction.get (ToolsAction.class),
	    SystemAction.get (PropertiesAction.class)
	};
    }
    
    protected String defaultDisplayName () {
        return NbBundle.getBundle(IORDataLoader.class).getString ("TXT_LoaderName");
    }
    
    protected void initialize () {
        super.initialize();
        ExtensionList eList = new ExtensionList();
        eList.addExtension ("ior");
        this.setExtensions (eList);
    }

}
