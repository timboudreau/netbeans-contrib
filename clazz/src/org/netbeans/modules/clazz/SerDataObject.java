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

package org.netbeans.modules.clazz;

import org.openide.filesystems.*;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.InstanceSupport;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

import org.openide.nodes.Node;
import org.openide.nodes.CookieSet;

import org.openide.util.HelpCtx;

/** DataObject which represents JavaBeans (".ser" files).
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jan Jancura, Ian Formanek, Dafe Simonek
*/
public final class SerDataObject extends ClassDataObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 8229229209013849842L;

    /** Constructs a new BeanDataObject */
    public SerDataObject(FileObject fo, ClassDataLoader loader) throws DataObjectExistsException {
        super (fo, loader);
    }
    
    /**
     * All serialized objects are treated as JavaBeans since the instance of
     * the object can be obtained (using deserialization).
     * @returns true
     */
    public boolean isJavaBean() {
        return true;
    }
    
    /**
     * All serialized objects can be copied using ordinary file copy.
     * @returns true
     */
    public boolean isCopyAllowed() {
        return true;
    }

    /**
     * Move is allowed iff the primary file can be written to.
     * @return true if the object can be moved.
     */
    public boolean isMoveAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    /**
     * Rename is allowed iff the primary file can be written to.
     * @return true if the object can be renamed.
     */
    public boolean isRenameAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    /** 
     * Creates NodeDelegate for this DataObject
     * @returns node that represent the obejct
    */
    protected Node createNodeDelegate () {
        return new SerDataNode (this);
    }

    /**
     * Provides special processing for the serialized objects:
     * if the help can be found for the class that is serialized inside
     * the ser file, returns that help. The default is the help for SerDataObject.
     * @returns appropriate HelpCtx
     */
    public HelpCtx getHelpCtx () {
        HelpCtx test = InstanceSupport.findHelp (instanceSupport);
        if (test != null)
            return test;
        else
            return new HelpCtx (SerDataObject.class);
    }

    protected void initCookies() {
        super.initCookies();
        CookieSet cs = getCookieSet();
        // since all serializable object can be treated as JavaBeans, 
        // always add an InstanceCookie.
        cs.add(instanceSupport);
    }
}
