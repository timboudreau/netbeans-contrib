/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.archiver;

import org.openide.loaders.*;
import org.openide.nodes.*;

public class ArchiveDataNode extends DataNode {
    
    public ArchiveDataNode(ArchiveDataObject obj) {
        this(obj, Children.LEAF);
    }
    
    public ArchiveDataNode(ArchiveDataObject obj, Children ch) {
        super(obj, ch);
        setIconBase("org/netbeans/modules/archiver/beans");
    }
    
    protected ArchiveDataObject getArchiveDataObject() {
        return (ArchiveDataObject)getDataObject();
    }
    
}
