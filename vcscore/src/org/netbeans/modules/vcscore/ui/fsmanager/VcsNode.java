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

package org.netbeans.modules.vcscore.ui.fsmanager;

import org.netbeans.modules.vcscore.registry.*;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.util.datatransfer.NewType;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;


/** 
 * Root node of Vcs FileSystems
 * @author Richard Gregor
 */
public class VcsNode extends AbstractNode {
    
    /** Array of the actions of the java methods, constructors and fields. */
    private static final SystemAction[] DEFAULT_ACTIONS = new SystemAction[0];
    
    public static final String ICON_BASE =
    "org/netbeans/modules/vcscore/actions/VcsManagerActionIcon"; // NOI18N       
    
    public VcsNode() {        
        super(new VcsChildren());                     
        setName( NbBundle.getBundle(VcsNode.class).getString("CTL_VcsNodeName"));
        setIconBase(ICON_BASE);
        setActions(DEFAULT_ACTIONS);
    }
    
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(VcsNode.class);
    }
    
    /** Set all actions for this node.
     * @param actions new list of actions
     */
    public void setActions(SystemAction[] actions) {
        systemActions = actions;
    }
    
    /** Serialization */
    public Node.Handle getHandle() {
        return new VcsHandle();
    }
    
    /** Handle for this node, it is serialized instead of node */
    static final class VcsHandle implements Node.Handle {
        static final long serialVersionUID =-3856331604791683300L;
        public Node getNode() {
            return new VcsNode();
        }
    }
    
}

