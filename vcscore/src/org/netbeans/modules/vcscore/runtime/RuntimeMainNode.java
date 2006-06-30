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
