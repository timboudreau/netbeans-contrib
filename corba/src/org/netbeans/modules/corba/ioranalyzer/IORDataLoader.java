/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
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
        super (IORDataObject.class);
        this.setDisplayName (NbBundle.getBundle(IORDataLoader.class).getString ("TXT_LoaderName"));
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
    
    protected void initialize () {
        ExtensionList eList = new ExtensionList();
        eList.addExtension ("ior");
        this.setExtensions (eList);
	setActions ( new SystemAction[] {
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
	});
    }

}