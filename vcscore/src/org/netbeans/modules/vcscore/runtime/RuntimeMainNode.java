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

package org.netbeans.modules.vcscore.runtime;

import java.beans.*;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;
import org.openide.filesystems.*;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsFileSystem;

/**
 * The folder node, which contains RuntimeFolderNodes nodes.
 *
 * @author  Milos Kleint
 */
public class RuntimeMainNode extends AbstractNode {
    
 
    public static final String VCS_RUNTIME_NODE_NAME = "VcsRuntime";
    
    /** Creates new RuntimeFolderNode */
    RuntimeMainNode(Children children) {
        super(children);
        setName(VCS_RUNTIME_NODE_NAME);
        setDisplayName(g("CTL_VcsRuntime"));
        setShortDescription(NbBundle.getMessage(RuntimeMainNode.class, "RuntimeMainNode.Description"));
        setIconBase("org/netbeans/modules/vcscore/runtime/commandIcon");
    }
    
    public RuntimeMainNode() {
        this(new RuntimeMainChildren());
    }
    

    private String g(String name) {
        return NbBundle.getMessage(RuntimeMainNode.class, name);
    }
    
}
