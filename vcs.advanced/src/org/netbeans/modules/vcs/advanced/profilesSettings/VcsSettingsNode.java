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

package org.netbeans.modules.vcs.advanced.profilesSettings;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Children.SortedArray;
import org.openide.nodes.CookieSet;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;


/** 
 * Subnodes of this node are nodes representing profile settings
 *
 * @author Richard Gregor
 */
public class VcsSettingsNode extends AbstractNode {
    
    /** Array of the actions of the java methods, constructors and fields. */
    private static final SystemAction[] DEFAULT_ACTIONS = new SystemAction[0];
    
    public static final String ICON_BASE =
    "org/netbeans/modules/vcs/advanced/vcsGeneric"; // NOI18N         
    
    public VcsSettingsNode() {        
        super(new VcsSettingsChildren());                     
        setName( NbBundle.getBundle(VcsSettingsNode.class).getString("LBL_VcsSettingsNode"));
        setIconBase(ICON_BASE);
        setActions(DEFAULT_ACTIONS);
    }
    
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(VcsSettingsNode.class);
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
        static final long serialVersionUID =-3256331604791682300L;
        public Node getNode() {
            return new VcsSettingsNode();
        }
    }
    
}

