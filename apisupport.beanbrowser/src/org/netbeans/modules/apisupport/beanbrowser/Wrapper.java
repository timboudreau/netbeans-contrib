/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.FilterNode.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/** The basic class--a wrapper for a node. */
public class Wrapper extends FilterNode {
    
    private Wrapper(Node orig) {
        super(orig, new WrapperKids(orig));
    }
    
    /** Create a wrapper node from an original.
     * Specially prevents recursion (creating a wrapper of a wrapper).
     */
    public static Node make(Node orig) {
        if (orig instanceof Wrapper) {
            // FQN to avoid interpretation as FilterNode.Children:
            org.openide.nodes.Children kids = new Children.Array();
            kids.add(new Node[] { orig.cloneNode() });
            AbstractNode toret = new AbstractNode(kids) {
                public HelpCtx getHelpCtx() {
                    return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                }
            };
            toret.setName("Already a wrapper node...");
            toret.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            return toret;
        } else {
            return new Wrapper(orig);
        }
    }
    
    public Node cloneNode() {
        return new Wrapper(getOriginal());
    }
    
    /*
    // Override to include special node-exploration action.
    public SystemAction[] getActions () {
        SystemAction[] orig = super.getActions ();
        if (orig == null) orig = new SystemAction[0];
        boolean includeSep = orig.length > 0 && orig[0] != null;
        SystemAction[] nue = new SystemAction[orig.length + (includeSep ? 2 : 1)];
        nue[0] = SystemAction.get (NodeExploreAction.class);
        if (includeSep) nue[1] = null;
        for (int i = 0; i < orig.length; i++)
            nue[i + (includeSep ? 2 : 1)] = orig[i];
        return nue;
    }
     
    // For access by NodeExploreAction:
    public Node getOriginal () {
        return super.getOriginal ();
    }
     */
    
}
